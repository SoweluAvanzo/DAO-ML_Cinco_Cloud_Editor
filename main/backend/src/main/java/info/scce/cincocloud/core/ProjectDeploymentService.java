package info.scce.cincocloud.core;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.time.Duration;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import info.scce.cincocloud.core.rest.types.PyroProjectDeployment;
import info.scce.cincocloud.core.rest.types.PyroProjectDeploymentStatus;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.StopProjectPodsTask;
import info.scce.cincocloud.k8s.ProjectK8SDeployment;
import info.scce.cincocloud.k8s.ProjectK8SIngress;
import info.scce.cincocloud.k8s.ProjectK8SPersistentVolume;
import info.scce.cincocloud.k8s.ProjectK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.ProjectK8SService;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.util.CDIUtils;
import info.scce.cincocloud.util.WaitUtils;

@ApplicationScoped
@Transactional
public class ProjectDeploymentService {

    @Inject
    ProjectWebSocket projectWebSocket;

    @Inject
    Vertx vertx;

    private final KubernetesClient client;

    public ProjectDeploymentService() {
        this.client = new DefaultKubernetesClient();
    }

    public PyroProjectDeployment deploy(PyroProjectDB project) {
        final var persistentVolumeClaim = new ProjectK8SPersistentVolumeClaim(client, project);
        final var persistentVolume = new ProjectK8SPersistentVolume(client, project);
        final var service = new ProjectK8SService(client, project);
        final var deployment = new ProjectK8SDeployment(client, persistentVolumeClaim, project);
        final var ingress = new ProjectK8SIngress(client, service, project);

        final var deployedDeploymentOptional = client.apps().statefulSets().list().getItems().stream()
                .filter(pod -> pod.getMetadata() != null)
                .filter(pod -> pod.getMetadata().getName().startsWith(deployment.getResource().getMetadata().getName()))
                .findFirst();

        // do not redeploy pods if they are still active
        // if any pod removal is scheduled, that task is removed
        if (deployedDeploymentOptional.isPresent()) {
            final var d = deployedDeploymentOptional.get();
            if (d.getStatus() != null && d.getStatus().getReadyReplicas() != null && d.getStatus().getReadyReplicas() == 1) {
                removeScheduledTasks(project);
                final var status = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.READY);
                projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));
                return status;
            }
        }

        if (getPersistentVolumeClaimByName(persistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumeClaims().create(persistentVolumeClaim.getResource());
        }

        if (getPersistentVolumeByName(persistentVolume.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumes().create(persistentVolume.getResource());
        }

        final var editorService = client.services().create(service.getResource());
        final var editorPod = client.apps().statefulSets().create(deployment.getResource());
        client.network().ingress().create(ingress.getResource());

        final var status = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.DEPLOYING);
        projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));

        waitUntilEditorPodIsReady(project, editorPod, editorService, ingress);

        return status;
    }

    private void waitUntilEditorPodIsReady(PyroProjectDB project, StatefulSet editorPod, Service editorService, ProjectK8SIngress ingress) {
        WaitUtils.asyncWaitUntil(
                vertx,
                () -> {
                    final var s = client.apps().statefulSets().list().getItems().stream()
                            .filter(s2 -> s2.getMetadata().getName().equals(editorPod.getMetadata().getName()))
                            .findFirst();

                    if (s.isEmpty() || s.get().getStatus() == null || s.get().getStatus().getReadyReplicas() == null) {
                        return false;
                    } else {
                        return s.get().getStatus().getReadyReplicas() == 1;
                    }
                },
                () -> {
                    final var webClient = WebClient.create(vertx);
                    waitUntilEditorIsReady(webClient, project, editorService, ingress);
                },
                () -> {
                    final var s2 = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.FAILED);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(1),
                Duration.ofSeconds(1)
        );
    }

    private void waitUntilEditorIsReady(WebClient webClient, PyroProjectDB project, Service service, ProjectK8SIngress ingress) {
        WaitUtils.asyncWaitUntil(
                vertx,
                webClient.get(service.getSpec().getPorts().get(0).getPort(), service.getSpec().getClusterIP(), "")
                        .send()
                        .map(res -> res.statusCode() == 200),
                () -> {
                    final var s2 = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.READY);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                () -> {
                    final var s2 = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.FAILED);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(1),
                Duration.ofSeconds(1)
        );
    }

    public void stop(PyroProjectDB project) {
        final var persistentVolumeClaim = new ProjectK8SPersistentVolumeClaim(client, project);
        final var service = new ProjectK8SService(client, project);
        final var deployment = new ProjectK8SDeployment(client, persistentVolumeClaim, project);
        final var ingress = new ProjectK8SIngress(client, service, project);

        client.services().delete(service.getResource());
        client.apps().statefulSets().delete(deployment.getResource());
        client.network().ingress().delete(ingress.getResource());
    }

    public void delete(PyroProjectDB project) {
        stop(project);

        final var persistentVolumeClaim = new ProjectK8SPersistentVolumeClaim(client, project);
        final var persistentVolume = new ProjectK8SPersistentVolume(client, project);

        client.persistentVolumeClaims().delete(persistentVolumeClaim.getResource());
        client.persistentVolumes().delete(persistentVolume.getResource());

        // no need to schedule pod removal if all resources are already deleted.
        removeScheduledTasks(project);
    }

    private Optional<PersistentVolumeClaim> getPersistentVolumeClaimByName(String name) {
        return client.persistentVolumeClaims().list().getItems().stream()
                .filter(s -> s.getMetadata().getName().equals(name))
                .findFirst();
    }

    private Optional<PersistentVolume> getPersistentVolumeByName(String name) {
        return client.persistentVolumes().list().getItems().stream()
                .filter(s -> s.getMetadata().getName().equals(name))
                .findFirst();
    }

    private void removeScheduledTasks(PyroProjectDB project) {
        StopProjectPodsTask.delete("projectId", project.id);
    }
}
