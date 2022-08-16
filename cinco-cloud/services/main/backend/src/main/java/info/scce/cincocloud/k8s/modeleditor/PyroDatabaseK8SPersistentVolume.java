package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.k8s.shared.K8SPersistentVolumeOptions;
import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroDatabaseK8SPersistentVolume extends PyroK8SResource<PersistentVolume> {

  private K8SPersistentVolumeOptions options;

  public PyroDatabaseK8SPersistentVolume(KubernetesClient client, ProjectDB project, K8SPersistentVolumeOptions options) {
    super(client, project);
    this.options = options;
    this.resource = build();
  }

  @Override
  protected PersistentVolume build() {
    return new PersistentVolumeBuilder()
        .withNewMetadata()
        .withNamespace(client.getNamespace())
        .withName(getProjectName() + "-database-pv-volume")
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(new PersistentVolumeSpecBuilder()
            .withStorageClassName(options.storageClassName)
            .withCapacity(Map.of("storage", Quantity.parse(options.storage)))
            .withClaimRef(new ObjectReferenceBuilder()
                .withNamespace(client.getNamespace())
                .withName(getProjectName() + "-database-pv-claim")
                .build())
            .withAccessModes("ReadWriteMany")
            .withHostPath(new HostPathVolumeSourceBuilder()
                .withPath(options.hostPath + "/workspaces/" + getProjectName() + "/database")
                .build())
            .build())
        .build();
  }
}
