package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
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
  private final String archetypeImageTag;
  private final boolean useSsl;

  public TheiaK8SDeployment(
      KubernetesClient client,
      TheiaK8SPersistentVolumeClaim persistentVolumeClaim,
      ProjectDB project,
      String archetypeImageTag,
      boolean useSsl
  ) {
    super(client, project);
    this.persistentVolumeClaim = persistentVolumeClaim;
    this.archetypeImageTag = archetypeImageTag;
    this.useSsl = useSsl;
    this.resource = build();
  }

  /**
   * Equivalent to:
   * <p>
   * apiVersion: apps/v1 kind: StatefulSet metadata: name: {name}-statefulset namespace: default labels: app: {name}
   * spec: serviceName: {name} replicas: 1 selector: matchLabels: app: {name} template: metadata: labels: app: {name}
   * spec: containers: - name: {name} image: registry.gitlab.com/scce/cinco-cloud-archetype/archetype:{archetypeImageTag}
   * imagePullPolicy: IfNotPresent ports: - containerPort: 3000 volumeMounts: - name: pv-data mountPath: /var/lib/{name}
   * volumes: - name: pv-data persistentVolumeClaim: claimName: {name}-pv-claim imagePullSecrets: - name:
   * gitlab-registry-secret
   *
   * @return the deployment.
   */
  @Override
  protected StatefulSet build() {
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
                        .withImage("registry.gitlab.com/scce/cinco-cloud-archetype/archetype:" + archetypeImageTag)
                        .withImagePullPolicy("IfNotPresent")
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
                                .withName("USE_SSL")
                                .withValue(String.valueOf(useSsl))
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
                        .withName("cinco-cloud-archetype-registry-secret")
                        .build())
                    .build())
                .build())
            .build())
        .build();
  }
}
