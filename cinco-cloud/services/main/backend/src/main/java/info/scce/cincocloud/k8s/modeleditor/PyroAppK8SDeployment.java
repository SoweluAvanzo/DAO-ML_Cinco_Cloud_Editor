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

  private final PyroAppK8SPersistentVolumeClaim persistentVolumeClaim;

  private final String host;

  private final String environment;

  private final String archetypeImage;

  private final String minioHost;
  private final String minioPort;
  private final String minioAccessKey;
  private final String minioSecretKey;

  public PyroAppK8SDeployment(
      KubernetesClient client,
      PyroAppK8SPersistentVolumeClaim persistentVolumeClaim,
      String host,
      String environment,
      String archetypeImage,
      String minioHost,
      String minioPort,
      String minioAccessKey,
      String minioSecretKey,
      ProjectDB project
  ) {
    super(client, project);
    this.persistentVolumeClaim = persistentVolumeClaim;
    this.host = host;
    this.environment = environment;
    this.archetypeImage = archetypeImage;
    this.minioHost = minioHost;
    this.minioPort = minioPort;
    this.minioAccessKey = minioAccessKey;
    this.minioSecretKey = minioSecretKey;
    this.resource = build();
  }

  @Override
  protected Deployment build() {
    final var name = getProjectName() + "-app-deployment";
    final var imagePullPolicy = environment.equals("local")
        ? "Never"
        : "IfNotPresent";

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
                .withImage(archetypeImage)
                .withImagePullPolicy(imagePullPolicy)
                .withPorts(
                    new ContainerPortBuilder()
                        .withContainerPort(3000)
                        .build(),
                    new ContainerPortBuilder()
                        .withContainerPort(443)
                        .build()
                )
                .withVolumeMounts(
                    new VolumeMountBuilder()
                      .withName("pv-data")
                      .withMountPath("/editor/workspace")
                      .build()
                )
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
                        .withName("INTERNAL_USE_SSL")
                        .withValue("false")
                        .build(),
                    new EnvVarBuilder()
                        .withName("INTERNAL_PYRO_HOST")
                        .withValue("localhost")
                        .build(),
                    new EnvVarBuilder()
                        .withName("INTERNAL_PYRO_PORT")
                        .withValue("443")
                        .build(),
                    new EnvVarBuilder()
                        .withName("INTERNAL_PYRO_SUBPATH")
                        .withValue("")
                        .build(),
                    new EnvVarBuilder()
                        .withName("EXTERNAL_USE_SSL")
                        .withValue("true")
                        .build(),
                    new EnvVarBuilder()
                        .withName("EXTERNAL_PYRO_HOST")
                        .withValue(host)
                        .build(),
                    new EnvVarBuilder()
                        .withName("EXTERNAL_PYRO_PORT")
                        .withValue("443")
                        .build(),
                    new EnvVarBuilder()
                        .withName("EXTERNAL_PYRO_SUBPATH")
                        .withValue("/workspaces/" + getProjectName() + "/pyro/")
                        .build(),
                    new EnvVarBuilder()
                        .withName("PYRO_SERVER_BINARIES_FILE")
                        .withValue("project-" + project.template.project.id + "-pyro-server-binaries.zip")
                        .build(),
                    new EnvVarBuilder()
                        .withName("MINIO_HOST")
                        .withValue(minioHost)
                        .build(),
                    new EnvVarBuilder()
                        .withName("MINIO_PORT")
                        .withValue(minioPort)
                        .build(),
                    new EnvVarBuilder()
                        .withName("MINIO_ACCESS_KEY")
                        .withValue(minioAccessKey)
                        .build(),
                    new EnvVarBuilder()
                        .withName("MINIO_SECRET_KEY")
                        .withValue(minioSecretKey)
                        .build()
                )
                .build()
        )
        .withVolumes(
            new VolumeBuilder()
              .withName("pv-data")
              .withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSourceBuilder()
                .withClaimName(persistentVolumeClaim.getResource().getMetadata().getName())
                .build()
              )
              .build()
        )
        .withRestartPolicy("Always")
        .endSpec()
        .endTemplate()
        .endSpec()
        .build();
  }
}
