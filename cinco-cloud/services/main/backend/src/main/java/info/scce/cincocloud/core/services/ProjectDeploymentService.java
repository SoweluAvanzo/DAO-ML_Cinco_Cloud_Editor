package info.scce.cincocloud.core.services;

import info.scce.cincocloud.config.Properties;
import info.scce.cincocloud.core.rest.tos.ProjectDeploymentStatus;
import info.scce.cincocloud.core.rest.tos.ProjectDeploymentTO;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;
import info.scce.cincocloud.k8s.K8SClientService;
import info.scce.cincocloud.k8s.K8SIngressService;
import info.scce.cincocloud.k8s.K8SUtils;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SDeployment;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SIngress;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SPersistentVolume;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SService;
import info.scce.cincocloud.k8s.shared.K8SPersistentVolumeOptions;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.util.CDIUtils;
import info.scce.cincocloud.util.WaitUtils;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.time.Duration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Transactional
public class ProjectDeploymentService {

    private static final int TIMEOUT_TIME_MIN = 10;
    private static final int DELAY_TIME_SEC = 1;

    @Inject
    ProjectWebSocket projectWebSocket;

    @Inject
    K8SClientService clientService;

    @Inject
    SettingsService settingsService;

    @Inject
    K8SIngressService ingressService;

    @Inject
    Vertx vertx;

    @Inject
    Properties properties;

    @ConfigProperty(name = "cincocloud.host")
    String host;

    @ConfigProperty(name = "cincocloud.environment")
    String environment;

    @ConfigProperty(name = "archetype.storage-class-name")
    String archetypeStorageClassName;

    @ConfigProperty(name = "archetype.storage")
    String archetypeStorage;

    @ConfigProperty(name = "archetype.host-path")
    String archetypeHostPath;

    @ConfigProperty(name = "archetype.create-persistent-volumes")
    boolean archetypeCreatePersistentVolumes;

    KubernetesClient client;

    K8SPersistentVolumeOptions pvOptions;

    void startup(@Observes StartupEvent event) {
        client = clientService.createClient();
        pvOptions = new K8SPersistentVolumeOptions(
                archetypeStorageClassName,
                archetypeStorage,
                archetypeHostPath,
                archetypeCreatePersistentVolumes);
    }

    public ProjectDeploymentTO deploy(ProjectDB project) {
        return deployLanguageEditor(project);
    }

    public ProjectDeploymentTO redeploy(ProjectDB project) {
        stop(project);
        return deploy(project);
    }

    public void stop(ProjectDB project) {
        stopLanguageEditor(project);
    }

    public void delete(ProjectDB project) {
        stopAndDeleteLanguageEditor(project);
    }

    private ProjectDeploymentTO deployLanguageEditor(ProjectDB project) {
        final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project, pvOptions);
        final var persistentVolume = new TheiaK8SPersistentVolume(client, project, pvOptions);
        final var service = new TheiaK8SService(client, project);
        final var deployment = new TheiaK8SDeployment(client, persistentVolumeClaim, project,
                settingsService.getSettings().archetypeImage, environment, properties.getMinioHost(),
                properties.getMinioPort(),
                properties.getMinioAccessKey(), properties.getMinioSecretKey());
        final var ingress = new TheiaK8SIngress(client, service, project, host, ingressService.getWorkspaceRootPath());

        final var deployedDeploymentOptional = client.apps().statefulSets().list().getItems().stream()
                .filter(pod -> pod.getMetadata() != null)
                .filter(pod -> pod.getMetadata().getName()
                        .startsWith(deployment.getResource().getMetadata().getName()))
                .findFirst();

        // do not redeploy pods if they are still active
        // if any pod removal is scheduled, that task is removed
        if (deployedDeploymentOptional.isPresent() && K8SUtils
                .isStatefulSetRunning(deployedDeploymentOptional.get())) {
            removeScheduledTasks(project);
            final var status = new ProjectDeploymentTO(ingress.getPath(),
                    ProjectDeploymentStatus.READY);
            projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));
            return status;
        }

        if (K8SUtils.getPersistentVolumeClaimByName(client,
                persistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumeClaims().create(persistentVolumeClaim.getResource());
        }

        if (archetypeCreatePersistentVolumes && K8SUtils
                .getPersistentVolumeByName(client, persistentVolume.getResource().getMetadata().getName())
                .isEmpty()) {
            client.persistentVolumes().create(persistentVolume.getResource());
        }

        final var editorServiceOptional = K8SUtils.getServiceByName(client,
                service.getResource().getMetadata().getName());

        final var editorService = editorServiceOptional.isEmpty()
                ? client.services().create(service.getResource())
                : editorServiceOptional.get();

        final var editorPodOptional = K8SUtils.getStatefulSetByName(client,
                deployment.getResource().getMetadata().getName());

        final var editorPod = editorPodOptional.isEmpty()
                ? client.apps().statefulSets().create(deployment.getResource())
                : editorPodOptional.get();

        if (K8SUtils.getIngressByName(client, ingress.getResource().getMetadata().getName()).isEmpty()) {
            client.network().v1().ingresses().create(ingress.getResource());
        }

        final var status = new ProjectDeploymentTO(ingress.getPath(),
                ProjectDeploymentStatus.DEPLOYING);
        projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));

        waitUntilTheaiPodIsReady(project, editorPod, editorService, ingress);

        return status;
    }

    private void waitUntilTheaiPodIsReady(ProjectDB project, StatefulSet statefulSet,
            Service service, TheiaK8SIngress ingress) {
        WaitUtils.asyncWaitUntil(
                Uni.createFrom().item(() -> {
                    final var s = K8SUtils.getStatefulSetByName(client, statefulSet.getMetadata().getName());
                    return s.isPresent() && K8SUtils.isStatefulSetRunning(s.get());
                }),
                () -> waitUntilAppIsReady(project, service, ingress.getPath()),
                () -> {
                    final var s2 = new ProjectDeploymentTO(ingress.getPath(), ProjectDeploymentStatus.FAILED);
                    final var socket = CDIUtils.getBean(ProjectWebSocket.class);
                    socket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(TIMEOUT_TIME_MIN),
                Duration.ofSeconds(DELAY_TIME_SEC));
    }

    private void waitUntilAppIsReady(ProjectDB project, Service service, String path) {
        final var webClient = WebClient.create(vertx);

        final var port = service.getSpec().getPorts().get(0).getPort();
        final var host = service.getSpec().getClusterIP();

        WaitUtils.asyncWaitUntil(
                webClient
                        .get(port, host, "")
                        .send()
                        .map(res -> {
                            final var status = res.statusCode();
                            final var body = res.bodyAsString();
                            return status == 200 && body.contains("theia-preload");
                        }),
                () -> {
                    final var s2 = new ProjectDeploymentTO(path, ProjectDeploymentStatus.READY);
                    final var socket = CDIUtils.getBean(ProjectWebSocket.class);
                    socket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                () -> {
                    final var s2 = new ProjectDeploymentTO(path, ProjectDeploymentStatus.FAILED);
                    final var socket = CDIUtils.getBean(ProjectWebSocket.class);
                    socket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(TIMEOUT_TIME_MIN),
                Duration.ofSeconds(DELAY_TIME_SEC));
    }

    private void stopLanguageEditor(ProjectDB project) {
        final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project, pvOptions);
        final var service = new TheiaK8SService(client, project);
        final var deployment = new TheiaK8SDeployment(client, persistentVolumeClaim, project,
                settingsService.getSettings().archetypeImage, environment, properties.getMinioHost(),
                properties.getMinioPort(),
                properties.getMinioAccessKey(), properties.getMinioSecretKey());
        final var ingress = new TheiaK8SIngress(client, service, project, host, ingressService.getWorkspaceRootPath());

        client.services().delete(service.getResource());
        client.apps().statefulSets().delete(deployment.getResource());
        client.network().v1().ingresses().delete(ingress.getResource());
    }

    private void stopAndDeleteLanguageEditor(ProjectDB project) {
        stopLanguageEditor(project);

        // remove the claim first so that the volume can be deleted

        final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project, pvOptions);
        forceDeletePersistentVolumeClaim(persistentVolumeClaim.getResource());

        if (archetypeCreatePersistentVolumes) {
            final var persistentVolume = new TheiaK8SPersistentVolume(client, project, pvOptions);
            forceDeletePersistentVolume(persistentVolume.getResource());
        }

        // no need to schedule pod removal if all resources are already deleted.
        removeScheduledTasks(project);
    }

    private void forceDeletePersistentVolume(PersistentVolume persistentVolume) {
        client.persistentVolumes()
                .withName(persistentVolume.getMetadata().getName())
                .withGracePeriod(0)
                .delete();
    }

    private void forceDeletePersistentVolumeClaim(PersistentVolumeClaim persistentVolumeClaim) {
        client.persistentVolumeClaims()
                .inNamespace(client.getNamespace())
                .withName(persistentVolumeClaim.getMetadata().getName())
                .withGracePeriod(0)
                .delete();
    }

    private void removeScheduledTasks(ProjectDB project) {
        StopProjectPodsTaskDB.delete("projectId", project.id);
    }
}
