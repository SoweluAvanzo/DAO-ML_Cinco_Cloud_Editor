package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
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

  public static Optional<Ingress> getIngressByName(KubernetesClient client, String name) {
    return  client.network().v1().ingresses().list().getItems().stream()
        .filter(i -> i.getMetadata().getName().equals(name))
        .findFirst();
  }

  public static boolean isDeploymentRunning(Deployment deployment) {
    if (deployment.getStatus() == null) return  false;

    final var desiredReplicas = deployment.getSpec().getReplicas();
    final var currentReplicas = deployment.getStatus().getReplicas();
    final var updatedReplicas = deployment.getStatus().getUpdatedReplicas();
    final var availableReplicas = deployment.getStatus().getAvailableReplicas();

    return desiredReplicas != null
            && desiredReplicas.equals(currentReplicas)
            && desiredReplicas.equals(updatedReplicas)
            && desiredReplicas.equals(availableReplicas);
  }

  public static boolean isStatefulSetRunning(StatefulSet statefulSet) {
    if (statefulSet.getStatus() == null) return  false;

    final var desiredReplicas = statefulSet.getSpec().getReplicas();
    final var currentReplicas = statefulSet.getStatus().getReplicas();
    final var updatedReplicas = statefulSet.getStatus().getUpdatedReplicas();
    final var availableReplicas = statefulSet.getStatus().getAvailableReplicas();

    return desiredReplicas != null
            && desiredReplicas.equals(currentReplicas)
            && desiredReplicas.equals(updatedReplicas)
            && desiredReplicas.equals(availableReplicas);
  }
}
