package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;
import info.scce.cincocloud.db.PyroProjectDB;

public class ProjectK8SDeployment extends ProjectK8SResource<StatefulSet> {

    private final ProjectK8SPersistentVolumeClaim persistentVolumeClaim;
    private final ProjectK8SService service;

    public ProjectK8SDeployment(
            KubernetesClient client,
            ProjectK8SPersistentVolumeClaim persistentVolumeClaim,
            ProjectK8SService service,
            PyroProjectDB project
    ) {
        super(client, project);
        this.persistentVolumeClaim = persistentVolumeClaim;
        this.service = service;
        this.resource = build();
    }

    /**
     * Equivalent to:
     *
     * apiVersion: apps/v1
     * kind: StatefulSet
     * metadata:
     *   name: {name}-statefulset
     *   namespace: default
     *   labels:
     *     app: {name}
     * spec:
     *   serviceName: {name}
     *   replicas: 1
     *   selector:
     *     matchLabels:
     *       app: {name}
     *   template:
     *     metadata:
     *       labels:
     *         app: {name}
     *     spec:
     *       containers:
     *         - name: {name}
     *           image: registry.gitlab.com/scce/cinco-cloud/editor
     *           ports:
     *             - containerPort: 3000
     *               name: {name}-port
     *           volumeMounts:
     *             - name: pv-data
     *               mountPath: /var/lib/{name}
     *       volumes:
     *         - name: pv-data
     *           persistentVolumeClaim:
     *             claimName: {name}-pv-claim
     *
     * @return the deployment.
     */
    @Override
    protected StatefulSet build() {
        return new StatefulSetBuilder()
                .withNewMetadata()
                    .withName(getProjectName() + "-statefulset")
                    .withNamespace(DEFAULT_NAMESPACE)
                    .withLabels(Map.of("app", getProjectName()))
                .endMetadata()
                .withSpec(new StatefulSetSpecBuilder()
                    .withServiceName(service.getResource().getMetadata().getName())
                    .withReplicas(1)
                    .withSelector(new LabelSelectorBuilder()
                            .withMatchLabels(Map.of("app", getProjectName()))
                            .build())
                    .withTemplate(new PodTemplateSpecBuilder()
                            .withNewMetadata()
                                    .withLabels(Map.of("app", getProjectName()))
                            .endMetadata()
                            .withSpec(new PodSpecBuilder()
                                    .withContainers(new ContainerBuilder()
                                            .withName(getProjectName())
                                            .withImage("registry.gitlab.com/scce/cinco-cloud/editor")
                                            .withPorts(new ContainerPortBuilder()
                                                    .withContainerPort(3000)
                                                    .withName(getProjectName() + "-port")
                                                    .build())
                                            .withVolumeMounts(new VolumeMountBuilder()
                                                    .withName("pv-data")
                                                    .withMountPath("/var/lib/" + getProjectName())
                                                    .build())
                                            .build())
                                    .withVolumes(new VolumeBuilder()
                                            .withName("pv-data")
                                            .withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSourceBuilder()
                                                    .withClaimName(persistentVolumeClaim.getResource().getMetadata().getName())
                                                    .build())
                                            .build())
                                    .build())
                            .build())
                    .build())
                .build();
    }
}
