package info.scce.cincocloud.k8s.modeleditor;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
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
import info.scce.cincocloud.k8s.K8SResource;

public class PyroDatabaseK8SDeployment extends PyroK8SResource<StatefulSet> {

    private final PyroDatabaseK8SPersistentVolumeClaim persistentVolumeClaim;

    public PyroDatabaseK8SDeployment(
            KubernetesClient client,
            PyroDatabaseK8SPersistentVolumeClaim persistentVolumeClaim,
            PyroProjectDB project
    ) {
        super(client, project);
        this.persistentVolumeClaim = persistentVolumeClaim;
        this.resource = build();
    }

    @Override
    protected StatefulSet build() {
        final var name = getProjectName() + "-database-statefulset";

        return new StatefulSetBuilder()
                .withNewMetadata()
                .withName(name)
                .withNamespace(K8SResource.DEFAULT_NAMESPACE)
                .withLabels(Map.of(
                        "app", name,
                        "project", String.valueOf(project.id)
                ))
                .endMetadata()
                .withSpec(new StatefulSetSpecBuilder()
                        .withServiceName(getProjectName())
                        .withReplicas(1)
                        .withSelector(new LabelSelectorBuilder()
                                .withMatchLabels(Map.of("app", name))
                                .build())
                        .withTemplate(new PodTemplateSpecBuilder()
                                .withNewMetadata()
                                .withLabels(Map.of("app", name))
                                .endMetadata()
                                .withSpec(new PodSpecBuilder()
                                        .withContainers(new ContainerBuilder()
                                                .withName(name)
                                                .withImage("docker.io/library/postgres:11.2")
                                                .withImagePullPolicy("IfNotPresent")
                                                .withPorts(new ContainerPortBuilder()
                                                        .withContainerPort(5432)
                                                        .build())
                                                .withVolumeMounts(new VolumeMountBuilder()
                                                        .withName("pv-data")
                                                        .withMountPath("/var/lib/postgresql/data")
                                                        .build())
                                                .withEnv(
                                                        new EnvVarBuilder()
                                                                .withName("POSTGRES_USER")
                                                                .withValue(getProjectName())
                                                                .build(),
                                                        new EnvVarBuilder()
                                                                .withName("POSTGRES_PASSWORD")
                                                                .withValue(getProjectName())
                                                                .build(),
                                                        new EnvVarBuilder()
                                                                .withName("POSTGRES_DB")
                                                                .withValue(getProjectName())
                                                                .build(),
                                                        new EnvVarBuilder()
                                                                .withName("PGDATA")
                                                                .withValue("/var/lib/postgresql/data/pgdata")
                                                                .build()
                                                )
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
