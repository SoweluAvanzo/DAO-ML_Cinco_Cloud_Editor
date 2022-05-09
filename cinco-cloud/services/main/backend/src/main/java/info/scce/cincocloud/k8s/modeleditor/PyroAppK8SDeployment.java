package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.ProjectDB;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroAppK8SDeployment extends PyroK8SResource<Deployment> {

  private final Service registryService;

  private final PyroAppK8SPersistentVolumeClaim persistentVolumeClaim;

  private final String host;

  private final String environment;

  public PyroAppK8SDeployment(
      KubernetesClient client,
      PyroAppK8SPersistentVolumeClaim persistentVolumeClaim,
      Service registryService,
      String host,
      String environment,
      ProjectDB project
  ) {
    super(client, project);
    this.persistentVolumeClaim = persistentVolumeClaim;
    this.registryService = registryService;
    this.host = host;
    this.environment = environment;
    this.resource = build();
  }

  @Override
  protected Deployment build() {
    final var name = getProjectName() + "-app-deployment";
    final var registryUrl =
        registryService.getSpec().getClusterIP() + ":" + registryService.getSpec().getPorts().get(0)
            .getPort();

    return new DeploymentBuilder()
        .withNewMetadata()
        .withName(name)
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", name, "project", String.valueOf(project.id)))
        .endMetadata()
        .withNewSpec()
        .withReplicas(1)
        .withSelector(new LabelSelectorBuilder()
            .withMatchLabels(Map.of("app", name))
            .build())
        .withNewTemplate()
        .withNewMetadata()
        .withLabels(Map.of("app", name))
        .endMetadata()
        .withNewSpec()
        .withContainers(
            new ContainerBuilder()
                .withName(name)
                .withImage(registryUrl + "/" + project.template.getImageName())
                .withImagePullPolicy("Always")
                .withPorts(
                    new ContainerPortBuilder()
                        .withContainerPort(3000)
                        .build(),
                    new ContainerPortBuilder()
                        .withContainerPort(443)
                        .build()
                )
                .withVolumeMounts(new VolumeMountBuilder()
                    .withName("pv-data")
                    .withMountPath("/editor/workspace")
                    .build())
                .withEnv(
                    new EnvVarBuilder()
                        .withName("DATABASE_URL")
                        .withValue(getProjectName() + "-database-service:5432/" + getProjectName())
                        .build(),
                    new EnvVarBuilder()
                        .withName("DATABASE_USER")
                        .withValue(getProjectName())
                        .build(),
                    new EnvVarBuilder()
                        .withName("DATABASE_PASSWORD")
                        .withValue(getProjectName())
                        .build(),
                    new EnvVarBuilder()
                        .withName("CINCO_CLOUD_HOST")
                        .withValue("main-service")
                        .build(),
                    new EnvVarBuilder()
                        .withName("CINCO_CLOUD_PORT")
                        .withValue("8000")
                        .build(),
                    new EnvVarBuilder()
                        .withName("CINCO_CLOUD_DEBUG")
                        .withValue("true")
                        .build(),
                    new EnvVarBuilder()
                        .withName("ENVIRONMENT")
                        .withValue(environment)
                        .build(),
                    new EnvVarBuilder()
                        .withName("PYRO_HOST")
                        .withValue(host)
                        .build(),
                    new EnvVarBuilder()
                        .withName("PYRO_PORT")
                        .withValue("443")
                        .build(),
                    new EnvVarBuilder()
                        .withName("PYRO_SUBPATH")
                        .withValue("/workspaces/" + getProjectName() + "/pyro/")
                        .build(),
                    new EnvVarBuilder()
                        .withName("USE_SSL")
                        .withValue("true")
                        .build()
                )
                .build()
        )
        .withVolumes(new VolumeBuilder()
            .withName("pv-data")
            .withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSourceBuilder()
                .withClaimName(persistentVolumeClaim.getResource().getMetadata().getName())
                .build())
            .build())
        .withRestartPolicy("Always")
        .withImagePullSecrets(new LocalObjectReferenceBuilder()
            .withName("cinco-cloud-registry-secret")
            .build())
        .endSpec()
        .endTemplate()
        .endSpec()
        .build();
  }
}
