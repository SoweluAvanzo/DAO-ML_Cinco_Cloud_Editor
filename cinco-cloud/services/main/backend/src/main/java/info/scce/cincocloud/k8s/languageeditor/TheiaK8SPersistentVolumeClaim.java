package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.k8s.shared.K8SPersistentVolumeOptions;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class TheiaK8SPersistentVolumeClaim extends TheiaK8SResource<PersistentVolumeClaim> {

  private final K8SPersistentVolumeOptions options;

  public TheiaK8SPersistentVolumeClaim(KubernetesClient client, ProjectDB project, K8SPersistentVolumeOptions options) {
    super(client, project);
    this.options = options;
    this.resource = build();
  }

  @Override
  protected PersistentVolumeClaim build() {
    var specs = new PersistentVolumeClaimSpecBuilder()
            .withStorageClassName(options.storageClassName)
            .withAccessModes("ReadWriteMany")
            .withResources(new ResourceRequirementsBuilder()
                    .withRequests(Map.of("storage", Quantity.parse(options.storage)))
                    .build());

    if (options.createPersistentVolumes) {
      specs = specs.withVolumeName(getProjectName() + "-pv-volume");
    }

    return new PersistentVolumeClaimBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-pv-claim")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(specs.build())
        .build();
  }
}
