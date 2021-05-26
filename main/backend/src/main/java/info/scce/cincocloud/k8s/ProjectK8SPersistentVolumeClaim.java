package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;
import info.scce.cincocloud.db.PyroProjectDB;

public class ProjectK8SPersistentVolumeClaim extends ProjectK8SResource<PersistentVolumeClaim> {

    public ProjectK8SPersistentVolumeClaim(KubernetesClient client, PyroProjectDB project) {
        super(client, project);
        this.resource = build();
    }

    /**
     * Equivalent to:
     *
     * apiVersion: v1
     * kind: PersistentVolumeClaim
     * metadata:
     *   name: {name}-pv-claim
     *   namespace: default
     *   labels:
     *     app: {name}
     * spec:
     *   storageClassName: manual
     *   accessModes:
     *     - ReadWriteMany
     *   resources:
     *     requests:
     *       storage: 2Gi
     *
     * @return The claim
     */
    @Override
    protected PersistentVolumeClaim build() {
        return new PersistentVolumeClaimBuilder()
                .withNewMetadata()
                    .withName(getProjectName() + "-pv-claim")
                    .withNamespace(DEFAULT_NAMESPACE)
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
