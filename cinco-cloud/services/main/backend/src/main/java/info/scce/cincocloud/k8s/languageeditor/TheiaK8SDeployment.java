package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
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

public class TheiaK8SDeployment extends TheiaK8SResource<StatefulSet> {

  private final TheiaK8SPersistentVolumeClaim persistentVolumeClaim;
  private final String archetypeImage;
  private final String environment;

  private final String minioHost;
  private final String minioPort;
  private final String minioAccessKey;
  private final String minioSecretKey;

  public TheiaK8SDeployment(
      KubernetesClient client,
      TheiaK8SPersistentVolumeClaim persistentVolumeClaim,
      ProjectDB project,
      String archetypeImage,
      String environment,
      String minioHost,
      String minioPort,
      String minioAccessKey,
      String minioSecretKey
  ) {
    super(client, project);
    this.persistentVolumeClaim = persistentVolumeClaim;
    this.archetypeImage = archetypeImage;
    this.environment = environment;
    this.minioHost = minioHost;
    this.minioPort = minioPort;
    this.minioAccessKey = minioAccessKey;
    this.minioSecretKey = minioSecretKey;
    this.resource = build();
  }

  /**
   * Equivalent to:
   * <p>
   * apiVersion: apps/v1 kind: StatefulSet metadata: name: {name}-statefulset namespace: default labels: app: {name}
   * spec: serviceName: {name} replicas: 1 selector: matchLabels: app: {name} template: metadata: labels: app: {name}
   * spec: containers: - name: {name} image: registry.gitlab.com/scce/cinco-cloud/archetype:tag
   * imagePullPolicy: IfNotPresent ports: - containerPort: 3000 volumeMounts: - name: pv-data mountPath: /var/lib/{name}
   * volumes: - name: pv-data persistentVolumeClaim: claimName: {name}-pv-claim imagePullSecrets: - name:
   * gitlab-registry-secret
   *
   * @return the deployment.
   */
  @Override
  protected StatefulSet build() {
    // if we are in a local development environment, we build the archetype ourselves
    // and pull it from the internal registry, hence, we use "Never" in this case.
    final var imagePullPolicy = environment.equals("local")
        ? "Never"
        : "IfNotPresent";

    return new StatefulSetBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-statefulset")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName(), "project", String.valueOf(project.id)))
        .endMetadata()
        .withSpec(new StatefulSetSpecBuilder()
            .withServiceName(getProjectName())
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
                        .withImage(archetypeImage)
                        .withImagePullPolicy(imagePullPolicy)
                        .withPorts(new ContainerPortBuilder()
                            .withContainerPort(3000)
                            .build())
                        .withVolumeMounts(new VolumeMountBuilder()
                            .withName("pv-data")
                            .withMountPath("/editor/workspace")
                            .build())
                        .withEnv(
                            new EnvVarBuilder()
                                .withName("CINCO_CLOUD_HOST")
                                .withValue("main-service")
                                .build(),
                            new EnvVarBuilder()
                                .withName("CINCO_CLOUD_PORT")
                                .withValue("8000")
                                .build(),
                            new EnvVarBuilder()
                                .withName("ENVIRONMENT")
                                .withValue(environment)
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
                                .build(),
                            new EnvVarBuilder()
                                .withName("USE_SSL")
                                .withValue("true")
                                .build()
                        )
                        .build())
                    .withVolumes(new VolumeBuilder()
                        .withName("pv-data")
                        .withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSourceBuilder()
                            .withClaimName(
                                persistentVolumeClaim.getResource().getMetadata().getName())
                            .build())
                        .build())
                    .withImagePullSecrets(new LocalObjectReferenceBuilder()
                        .withName("cinco-cloud-registry-secret")
                        .build())
                    .build())
                .build())
            .build())
        .build();
  }
}
