package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Optional;

public class K8SUtils {

  public static Optional<PersistentVolumeClaim> getPersistentVolumeClaimByName(
      KubernetesClient client, String name) {
    return client.persistentVolumeClaims().list().getItems().stream()
        .filter(s -> s.getMetadata().getName().equals(name))
        .findFirst();
  }

  public static Optional<PersistentVolume> getPersistentVolumeByName(KubernetesClient client,
      String name) {
    return client.persistentVolumes().list().getItems().stream()
        .filter(s -> s.getMetadata().getName().equals(name))
        .findFirst();
  }

  public static Optional<Service> getServiceByName(KubernetesClient client, String name) {
    return client.services().list().getItems().stream()
        .filter(s -> s.getMetadata().getName().equals(name))
        .findFirst();
  }

  public static Optional<Deployment> getDeploymentByName(KubernetesClient client, String name) {
    return client.apps().deployments().list().getItems().stream()
        .filter(s2 -> s2.getMetadata().getName().equals(name))
        .findFirst();
  }

  public static Optional<StatefulSet> getStatefulSetByName(KubernetesClient client, String name) {
    return client.apps().statefulSets().list().getItems().stream()
        .filter(s2 -> s2.getMetadata().getName().equals(name))
        .findFirst();
  }

  public static boolean isDeploymentRunning(Deployment deployment) {
    final var status = deployment.getStatus();
    return status != null && status.getReadyReplicas() != null && status.getReadyReplicas() >= 1;
  }

  public static boolean isStatefulSetRunning(StatefulSet statefulSet) {
    final var status = statefulSet.getStatus();
    return status != null && status.getReadyReplicas() != null && status.getReadyReplicas() >= 1;
  }
}
