package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class TheiaK8SService extends TheiaK8SResource<Service> {

  public TheiaK8SService(KubernetesClient client, ProjectDB project) {
    super(client, project);
    this.resource = build();
  }

  /**
   * Equivalent to:
   * <p>
   * apiVersion: v1 kind: Service metadata: name: {name}-service namespace: default labels: app: {name} spec: ports: -
   * port: 3000 protocol: TCP type: NodePort selector: app: {name}
   *
   * @return the service
   */
  @Override
  protected Service build() {
    return new ServiceBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-service")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(new ServiceSpecBuilder()
            .withPorts(new ServicePortBuilder()
                .withPort(3000)
                .withProtocol("TCP")
                .build())
            .withType("NodePort")
            .withSelector(Map.of("app", getProjectName()))
            .build())
        .build();
  }
}
