package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.k8s.languageeditor.TheiaK8SResource;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroAppK8SPersistentVolumeClaim extends TheiaK8SResource<PersistentVolumeClaim> {

  private static final String STORAGE = "2Gi";

  public PyroAppK8SPersistentVolumeClaim(KubernetesClient client, ProjectDB project) {
    super(client, project);
    this.resource = build();
  }

  @Override
  protected PersistentVolumeClaim build() {
    return new PersistentVolumeClaimBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-app-pv-claim")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(new PersistentVolumeClaimSpecBuilder()
            .withStorageClassName("manual")
            .withAccessModes("ReadWriteMany")
            .withResources(new ResourceRequirementsBuilder()
                .withRequests(Map.of("storage", Quantity.parse(STORAGE)))
                .build())
            .build()
        )
        .build();
  }
}
