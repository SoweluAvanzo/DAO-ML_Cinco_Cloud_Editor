package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class TheiaK8SPersistentVolumeClaim extends TheiaK8SResource<PersistentVolumeClaim> {

  public TheiaK8SPersistentVolumeClaim(KubernetesClient client, ProjectDB project) {
    super(client, project);
    this.resource = build();
  }

  /**
   * Equivalent to:
   * <p>
   * apiVersion: v1 kind: PersistentVolumeClaim metadata: name: {name}-pv-claim namespace: default labels: app: {name}
   * spec: storageClassName: manual accessModes: - ReadWriteMany resources: requests: storage: 2Gi
   *
   * @return The claim
   */
  @Override
  protected PersistentVolumeClaim build() {
    return new PersistentVolumeClaimBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-pv-claim")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(new PersistentVolumeClaimSpecBuilder()
            .withStorageClassName("manual")
            .withAccessModes("ReadWriteMany")
            .withResources(new ResourceRequirementsBuilder()
                .withRequests(Map.of("storage", Quantity.parse("2Gi")))
                .build())
            .build()
        )
        .build();
  }
}
