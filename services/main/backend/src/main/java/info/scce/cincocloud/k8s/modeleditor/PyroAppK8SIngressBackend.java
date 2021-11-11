package info.scce.cincocloud.k8s.modeleditor;

import info.scce.cincocloud.db.PyroProjectDB;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressPathBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressRuleValueBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackendBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressRuleBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressServiceBackendBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.IngressSpecBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.ServiceBackendPortBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;

public class PyroAppK8SIngressBackend extends PyroK8SResource<Ingress> {

  private final PyroAppK8SService service;
  private final String host;

  public PyroAppK8SIngressBackend(KubernetesClient client, PyroAppK8SService service,
      PyroProjectDB project, String host) {
    super(client, project);
    this.service = service;
    this.host = host;
    this.resource = build();
  }

  @Override
  protected Ingress build() {
    final var path = "/workspaces/" + getProjectName() + "/pyro(/|$)(.*)";

    return new IngressBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-app-ingress-backend")
        .withAnnotations(Map.of(
            "nginx.ingress.kubernetes.io/rewrite-target", "/$2",
            "nginx.ingress.kubernetes.io/use-regex", "true"))
        .endMetadata()
        .withSpec(new IngressSpecBuilder()
            .withRules(new IngressRuleBuilder()
                .withHost(host)
                .withHttp(new HTTPIngressRuleValueBuilder()
                    .withPaths(new HTTPIngressPathBuilder()
                        .withPath(path)
                        .withPathType("Prefix")
                        .withBackend(new IngressBackendBuilder()
                            .withService(new IngressServiceBackendBuilder()
                                .withName(service.getResource().getMetadata().getName())
                                .withPort(new ServiceBackendPortBuilder()
                                    .withNumber(service.getBackendPort())
                                    .build()
                                )
                                .build()
                            )
                            .build())
                        .build())
                    .build())
                .build())
            .build())
        .build();
  }
}
