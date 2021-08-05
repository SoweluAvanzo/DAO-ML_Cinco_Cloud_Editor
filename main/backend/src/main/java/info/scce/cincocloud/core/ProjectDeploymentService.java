package info.scce.cincocloud.core;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import info.scce.cincocloud.core.rest.types.PyroProjectDeployment;
import info.scce.cincocloud.core.rest.types.PyroProjectDeploymentStatus;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.StopProjectPodsTask;
import info.scce.cincocloud.k8s.K8SException;
import info.scce.cincocloud.k8s.K8SUtils;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SDeployment;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SIngress;
import info.scce.cincocloud.k8s.modeleditor.PyroAppK8SService;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SDeployment;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SPersistentVolume;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.modeleditor.PyroDatabaseK8SService;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SDeployment;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SIngress;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SPersistentVolume;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SService;
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
        if (project.isLanguageEditor()) {
            return deployLanguageEditor(project);
        } else {
            return deployModelEditor(project);
        }
    }

    public void stop(PyroProjectDB project) {
        if (project.isLanguageEditor()) {
            stopLanguageEditor(project);
        } else {
            stopModelEditor(project);
        }
    }

    public void delete(PyroProjectDB project) {
        if (project.image == null) {
            stopAndDeleteLanguageEditor(project);
        } else {
            stopAndDeleteModelEditor(project);
        }
    }

    private PyroProjectDeployment deployModelEditor(PyroProjectDB project) {
        // create modeleditor app resources
        final var appService = new PyroAppK8SService(client, project);
        final var appDeployment = new PyroAppK8SDeployment(client, getRegistryService(), project);
        final var appIngress = new PyroAppK8SIngress(client, appService, project);

        // create modeleditor database resources
        final var databaseService = new PyroDatabaseK8SService(client, project);
        final var databasePersistentVolume = new PyroDatabaseK8SPersistentVolume(client, project);
        final var databasePersistentVolumeClaim = new PyroDatabaseK8SPersistentVolumeClaim(client, project);
        final var databaseDeployment = new PyroDatabaseK8SDeployment(client, databasePersistentVolumeClaim, project);

        final var deployedAppOptional = client.apps().deployments().list().getItems().stream()
                .filter(pod -> pod.getMetadata() != null)
                .filter(pod -> pod.getMetadata().getName().startsWith(appDeployment.getResource().getMetadata().getName()))
                .findFirst();

        // do not redeploy pods if they are still active
        // if any pod removal is scheduled, that task is removed
        if (deployedAppOptional.isPresent() && K8SUtils.isDeploymentRunning(deployedAppOptional.get())) {
            removeScheduledTasks(project);
            final var status = new PyroProjectDeployment(appIngress.getPath(), PyroProjectDeploymentStatus.READY);
            projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));
            return status;
        }

        if (K8SUtils.getPersistentVolumeClaimByName(client, databasePersistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumeClaims().create(databasePersistentVolumeClaim.getResource());
        }

        if (K8SUtils.getPersistentVolumeByName(client, databasePersistentVolume.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumes().create(databasePersistentVolume.getResource());
        }

        // start database
        client.services().create(databaseService.getResource());
        client.apps().statefulSets().create(databaseDeployment.getResource());

        // start modeleditor app
        final var service = client.services().create(appService.getResource());
        final var deployment = client.apps().deployments().create(appDeployment.getResource());
        client.network().ingress().create(appIngress.getResource());

        final var status = new PyroProjectDeployment(appIngress.getPath(), PyroProjectDeploymentStatus.DEPLOYING);
        projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));

        waitUntilPyroPodIsReady(project, deployment, service, appIngress);

        return status;
    }

    private PyroProjectDeployment deployLanguageEditor(PyroProjectDB project) {
        final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project);
        final var persistentVolume = new TheiaK8SPersistentVolume(client, project);
        final var service = new TheiaK8SService(client, project);
        final var deployment = new TheiaK8SDeployment(client, persistentVolumeClaim, project);
        final var ingress = new TheiaK8SIngress(client, service, project);

        final var deployedDeploymentOptional = client.apps().statefulSets().list().getItems().stream()
                .filter(pod -> pod.getMetadata() != null)
                .filter(pod -> pod.getMetadata().getName().startsWith(deployment.getResource().getMetadata().getName()))
                .findFirst();

        // do not redeploy pods if they are still active
        // if any pod removal is scheduled, that task is removed
        if (deployedDeploymentOptional.isPresent() && K8SUtils.isStatefulSetRunning(deployedDeploymentOptional.get())) {
            removeScheduledTasks(project);
            final var status = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.READY);
            projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));
            return status;
        }

        if (K8SUtils.getPersistentVolumeClaimByName(client, persistentVolumeClaim.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumeClaims().create(persistentVolumeClaim.getResource());
        }

        if (K8SUtils.getPersistentVolumeByName(client, persistentVolume.getResource().getMetadata().getName()).isEmpty()) {
            client.persistentVolumes().create(persistentVolume.getResource());
        }

        final var editorService = client.services().create(service.getResource());
        final var editorPod = client.apps().statefulSets().create(deployment.getResource());
        client.network().ingress().create(ingress.getResource());

        final var status = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.DEPLOYING);
        projectWebSocket.send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(status));

        waitUntilTheaiPodIsReady(project, editorPod, editorService, ingress);

        return status;
    }

    private void waitUntilPyroPodIsReady(PyroProjectDB project, Deployment deployment, Service service, PyroAppK8SIngress ingress) {
        WaitUtils.asyncWaitUntil(
                vertx,
                () -> {
                    final var s = K8SUtils.getDeploymentByName(client, deployment.getMetadata().getName());
                    return s.isPresent() && K8SUtils.isDeploymentRunning(s.get());
                },
                () -> {
                    final var webClient = WebClient.create(vertx);
                    waitUntilAppIsReady(webClient, project, service, ingress.getPath());
                },
                () -> {
                    final var s2 = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.FAILED);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(1),
                Duration.ofSeconds(1)
        );
    }

    private void waitUntilTheaiPodIsReady(PyroProjectDB project, StatefulSet statefulSet, Service service, TheiaK8SIngress ingress) {
        WaitUtils.asyncWaitUntil(
                vertx,
                () -> {
                    final var s = K8SUtils.getStatefulSetByName(client,statefulSet.getMetadata().getName());
                    return s.isPresent() && K8SUtils.isStatefulSetRunning(s.get());
                },
                () -> {
                    final var webClient = WebClient.create(vertx);
                    waitUntilAppIsReady(webClient, project, service, ingress.getPath());
                },
                () -> {
                    final var s2 = new PyroProjectDeployment(ingress.getPath(), PyroProjectDeploymentStatus.FAILED);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(1),
                Duration.ofSeconds(1)
        );
    }

    private void waitUntilAppIsReady(WebClient webClient, PyroProjectDB project, Service service, String path) {
        WaitUtils.asyncWaitUntil(
                vertx,
                webClient.get(service.getSpec().getPorts().get(0).getPort(), service.getSpec().getClusterIP(), "")
                        .send()
                        .map(res -> res.statusCode() == 200 || res.statusCode() == 403),
                () -> {
                    final var s2 = new PyroProjectDeployment(path, PyroProjectDeploymentStatus.READY);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                () -> {
                    final var s2 = new PyroProjectDeployment(path, PyroProjectDeploymentStatus.FAILED);
                    CDIUtils.getBean(ProjectWebSocket.class).send(project.id, ProjectWebSocket.Messages.podDeploymentStatus(s2));
                },
                Duration.ofMinutes(1),
                Duration.ofSeconds(1)
        );
    }

    public void stopModelEditor(PyroProjectDB project) {
        final var appService = new PyroAppK8SService(client, project);
        final var appDeployment = new PyroAppK8SDeployment(client, getRegistryService(), project);
        final var appIngress = new PyroAppK8SIngress(client, appService, project);

        final var databaseService = new PyroDatabaseK8SService(client, project);
        final var databasePersistentVolumeClaim = new PyroDatabaseK8SPersistentVolumeClaim(client, project);
        final var databaseDeployment = new PyroDatabaseK8SDeployment(client, databasePersistentVolumeClaim, project);

        client.services().delete(appService.getResource());
        client.apps().deployments().delete(appDeployment.getResource());
        client.network().ingress().delete(appIngress.getResource());

        client.services().delete(databaseService.getResource());
        client.apps().statefulSets().delete(databaseDeployment.getResource());
    }

    private void stopLanguageEditor(PyroProjectDB project) {
        final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project);
        final var service = new TheiaK8SService(client, project);
        final var deployment = new TheiaK8SDeployment(client, persistentVolumeClaim, project);
        final var ingress = new TheiaK8SIngress(client, service, project);

        client.services().delete(service.getResource());
        client.apps().statefulSets().delete(deployment.getResource());
        client.network().ingress().delete(ingress.getResource());
    }

    private void stopAndDeleteModelEditor(PyroProjectDB project) {
        stopModelEditor(project);

        final var persistentVolume = new PyroDatabaseK8SPersistentVolume(client, project);
        final var persistentVolumeClaim = new PyroDatabaseK8SPersistentVolumeClaim(client, project);

        // remove the claim first so that the volume can be deleted
        client.persistentVolumeClaims().delete(persistentVolumeClaim.getResource());
        client.persistentVolumes().delete(persistentVolume.getResource());

        // no need to schedule pod removal if all resources are already deleted.
        removeScheduledTasks(project);
    }

    private void stopAndDeleteLanguageEditor(PyroProjectDB project) {
        stopLanguageEditor(project);

        final var persistentVolumeClaim = new TheiaK8SPersistentVolumeClaim(client, project);
        final var persistentVolume = new TheiaK8SPersistentVolume(client, project);

        // remove the claim first so that the volume can be deleted
        client.persistentVolumeClaims().delete(persistentVolumeClaim.getResource());
        client.persistentVolumes().delete(persistentVolume.getResource());

        // no need to schedule pod removal if all resources are already deleted.
        removeScheduledTasks(project);
    }

    private void removeScheduledTasks(PyroProjectDB project) {
        StopProjectPodsTask.delete("projectId", project.id);
    }

    private Service getRegistryService() {
        return K8SUtils.getServiceByName(client, "registry-service")
                .orElseThrow(() -> new K8SException("could not find registryService."));
    }
}
