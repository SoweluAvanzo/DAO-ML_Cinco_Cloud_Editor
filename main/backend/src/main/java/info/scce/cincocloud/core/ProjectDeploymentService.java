package info.scce.cincocloud.core;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import info.scce.cincocloud.core.rest.types.PyroProjectDeployment;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.k8s.K8SException;
import info.scce.cincocloud.k8s.K8SResource;
import info.scce.cincocloud.k8s.ProjectK8SDeployment;
import info.scce.cincocloud.k8s.ProjectK8SIngress;
import info.scce.cincocloud.k8s.ProjectK8SPersistentVolume;
import info.scce.cincocloud.k8s.ProjectK8SPersistentVolumeClaim;
import info.scce.cincocloud.k8s.ProjectK8SService;
import info.scce.cincocloud.sync.ProjectWebSocket;

@ApplicationScoped
@Transactional
public class ProjectDeploymentService {

    @Inject
    ProjectWebSocket projectWebSocket;

    private final KubernetesClient client;

    public ProjectDeploymentService() {
        this.client = new DefaultKubernetesClient();
    }

    public PyroProjectDeployment deploy(PyroProjectDB project) {
        final var persistentVolumeClaim = new ProjectK8SPersistentVolumeClaim(client, project);
        final var persistentVolume = new ProjectK8SPersistentVolume(client, project);
        final var service = new ProjectK8SService(client, project);
        final var deployment = new ProjectK8SDeployment(client, persistentVolumeClaim, service, project);
        final var ingress = new ProjectK8SIngress(client, service, project);

        final var result = new PyroProjectDeployment();
        result.setUrl(ingress.getPath());

        final var deployedDeploymentOptional = client.apps().statefulSets().list().getItems().stream()
                .filter(pod -> pod.getMetadata().getName().startsWith(deployment.getResource().getMetadata().getName()))
                .findFirst();

        if (deployedDeploymentOptional.isPresent() && deployedDeploymentOptional.get().getStatus().getReadyReplicas() == 1) {
            return result;
        }

        if (client.persistentVolumeClaims().list().getItems().stream()
                .noneMatch(pvc -> pvc.getMetadata().getName().equals(persistentVolumeClaim.getResource().getMetadata().getName()))) {
            client.persistentVolumeClaims().create(persistentVolumeClaim.getResource());
        }

        if (client.persistentVolumes().list().getItems().stream()
                .noneMatch(pv -> pv.getMetadata().getName().equals(persistentVolume.getResource().getMetadata().getName()))) {
            client.persistentVolumes().create(persistentVolume.getResource());
        }

        stop(project);

        client.services().create(service.getResource());
        client.apps().statefulSets().create(deployment.getResource());
        client.network().ingress().create(ingress.getResource());

        return result;
    }

    public void stop(PyroProjectDB project) {
        final var persistentVolumeClaim = new ProjectK8SPersistentVolumeClaim(client, project);
        final var service = new ProjectK8SService(client, project);
        final var deployment = new ProjectK8SDeployment(client, persistentVolumeClaim, service, project);
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
    }
}
