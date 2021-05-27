package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;
import info.scce.cincocloud.db.PyroProjectDB;

public class ProjectK8SPersistentVolume extends ProjectK8SResource<PersistentVolume> {

    public ProjectK8SPersistentVolume(KubernetesClient client, PyroProjectDB project) {
        super(client, project);
        this.resource = build();
    }

    /**
     * Equivalent to:
     *
     * apiVersion: v1
     * kind: PersistentVolume
     * metadata:
     *   name: {name}-pv-volume
     *   namespace: default
     *   labels:
     *     app: {name}
     * spec:
     *   storageClassName: manual
     *   capacity:
     *     storage: 2Gi
     *   accessModes:
     *     - ReadWriteMany
     *   hostPath:
     *     path: "/mnt/data/workspaces/{name}"
     *
     * @return the volume
     */
    @Override
    protected PersistentVolume build() {
        return new PersistentVolumeBuilder()
                .withNewMetadata()
                    .withNamespace(DEFAULT_NAMESPACE)
                    .withName(getProjectName() + "-pv-volume")
                    .withLabels(Map.of("app", getProjectName()))
                .endMetadata()
                .withSpec(new PersistentVolumeSpecBuilder()
                    .withStorageClassName("manual")
                    .withCapacity(Map.of("storage", Quantity.parse("2Gi")))
                    .withAccessModes("ReadWriteMany")
                    .withHostPath(new HostPathVolumeSourceBuilder()
                        .withPath("/mnt/data/workspaces/" + getProjectName())
                        .build())
                    .build())
                .build();
    }
}
