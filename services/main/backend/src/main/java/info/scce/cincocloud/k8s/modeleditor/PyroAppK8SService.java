package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.PyroProjectDB;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroAppK8SService extends PyroK8SResource<Service> {

  public PyroAppK8SService(KubernetesClient client, PyroProjectDB project) {
    super(client, project);
    this.resource = build();
  }

  @Override
  protected Service build() {
    return new ServiceBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-app-service")
        .withNamespace(client.getNamespace())
        .withLabels(Map.of("app", getProjectName()))
        .endMetadata()
        .withSpec(new ServiceSpecBuilder()
            .withPorts(
                new ServicePortBuilder()
                    .withName("theia")
                    .withPort(getFrontendPort())
                    .withProtocol("TCP")
                    .build(),
                new ServicePortBuilder()
                    .withName("pyro")
                    .withPort(getBackendPort())
                    .withProtocol("TCP")
                    .build()
            )
            .withType("NodePort")
            .withSelector(Map.of("app", getProjectName() + "-app-deployment"))
            .build())
        .build();
  }

  public int getFrontendPort() {
    return 3000;
  }

  public int getBackendPort() {
    return 80;
  }
}
