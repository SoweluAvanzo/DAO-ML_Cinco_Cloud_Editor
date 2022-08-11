package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.k8s.shared.K8SPersistentVolumeOptions;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroDatabaseK8SPersistentVolumeClaim extends PyroK8SResource<PersistentVolumeClaim> {

  private K8SPersistentVolumeOptions options;

  public PyroDatabaseK8SPersistentVolumeClaim(KubernetesClient client, ProjectDB project, K8SPersistentVolumeOptions options) {
    super(client, project);
    this.options = options;
    this.resource = build();
  }

  @Override
  protected PersistentVolumeClaim build() {
    return new PersistentVolumeClaimBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-database-pv-claim")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(new PersistentVolumeClaimSpecBuilder()
            .withStorageClassName(options.storageClassName)
            .withVolumeName(getProjectName() + "-database-pv-volume")
            .withAccessModes("ReadWriteMany")
            .withResources(new ResourceRequirementsBuilder()
                .withRequests(Map.of("storage", Quantity.parse(options.storage)))
                .build())
            .build()
        )
        .build();
  }
}
