package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
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

public class TheiaK8SIngress extends TheiaK8SResource<Ingress> {

  private final TheiaK8SService service;
  private final String host;
  private final String rootPath;

  public TheiaK8SIngress(KubernetesClient client, TheiaK8SService service, ProjectDB project, String host,
      String rootPath) {
    super(client, project);
    this.service = service;
    this.host = host;
    this.rootPath = rootPath;
    this.resource = build();
  }

  /**
   * Equivalent to:
   * <p>
   * apiVersion: networking.k8s.io/v1 kind: Ingress metadata: name: {name}-ingress annotations:
   * nginx.ingress.kubernetes.io/add-base-url: "true" nginx.ingress.kubernetes.io/rewrite-target: /$2 spec: rules: -
   * host: cinco-cloud http: paths: - path: /workspaces/{name}(/|$)(.*) pathType: Prefix backend: service: name:
   * {name}-service port: name: {name}-port
   *
   * @return the ingress controller.
   */
  @Override
  protected Ingress build() {
    final var path = getPath().substring(0, getPath().length() - 1) + "(/|$)(.*)";
    final var wsPath = getPath().substring(0, getPath().length() - 1) + "/ws" + "(/|$)(.*)";
    final var resourcePath = getPath().substring(0, getPath().length() - 1) + "/web" + "(/|$)(.*)";

    return new IngressBuilder()
        .withNewMetadata()
        .withName(getProjectName() + "-ingress")
        .withAnnotations(Map.of(
            "nginx.ingress.kubernetes.io/add-base-url", "true",
            "nginx.ingress.kubernetes.io/rewrite-target", "/$2",
            "nginx.ingress.kubernetes.io/proxy-read-timeout", "3600",
            "nginx.ingress.kubernetes.io/proxy-send-timeout", "3600",
            "nginx.ingress.kubernetes.io/proxy-body-size", "8m"))
        .endMetadata()
        .withSpec(new IngressSpecBuilder()
            .withRules(new IngressRuleBuilder()
                .withHost(host)
                .withHttp(new HTTPIngressRuleValueBuilder()
                    .withPaths(
                        new HTTPIngressPathBuilder()
                        .withPath(path)
                        .withPathType("Prefix")
                        .withBackend(new IngressBackendBuilder()
                            .withService(new IngressServiceBackendBuilder()
                                .withName(service.getResource().getMetadata().getName())
                                .withPort(new ServiceBackendPortBuilder()
                                    .withNumber(
                                        service.getResource().getSpec().getPorts().get(0).getPort())
                                    .build()
                                )
                                .build()
                            )
                            .build())
                        .build(),
                        new HTTPIngressPathBuilder()
                        .withPath(wsPath)
                        .withPathType("Prefix")
                        .withBackend(new IngressBackendBuilder()
                            .withService(new IngressServiceBackendBuilder()
                                .withName(service.getResource().getMetadata().getName())
                                .withPort(new ServiceBackendPortBuilder()
                                    .withNumber(
                                        service.getResource().getSpec().getPorts().get(1).getPort())
                                    .build()
                                )
                                .build()
                            )
                            .build())
                        .build(),
                        new HTTPIngressPathBuilder()
                        .withPath(resourcePath)
                        .withPathType("Prefix")
                        .withBackend(new IngressBackendBuilder()
                            .withService(new IngressServiceBackendBuilder()
                                .withName(service.getResource().getMetadata().getName())
                                .withPort(new ServiceBackendPortBuilder()
                                    .withNumber(
                                        service.getResource().getSpec().getPorts().get(2).getPort())
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

  public String getPath() {
    return rootPath + "/" + getProjectName() + "/";
  }
}
