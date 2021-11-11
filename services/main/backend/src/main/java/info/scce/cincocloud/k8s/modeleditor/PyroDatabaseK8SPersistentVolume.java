package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.PyroProjectDB;
import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroDatabaseK8SPersistentVolume extends PyroK8SResource<PersistentVolume> {

  private static final String STORAGE = "2Gi";

  public PyroDatabaseK8SPersistentVolume(KubernetesClient client, PyroProjectDB project) {
    super(client, project);
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
            .withStorageClassName("manual")
            .withCapacity(Map.of("storage", Quantity.parse(STORAGE)))
            .withAccessModes("ReadWriteMany")
            .withHostPath(new HostPathVolumeSourceBuilder()
                .withPath("/mnt/data/workspaces/" + getProjectName() + "/database")
                .build())
            .build())
        .build();
  }
}
