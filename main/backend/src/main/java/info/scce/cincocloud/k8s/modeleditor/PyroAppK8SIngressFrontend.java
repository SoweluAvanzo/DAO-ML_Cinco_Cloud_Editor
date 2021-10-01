package info.scce.cincocloud.k8s.modeleditor;

import io.fabric8.kubernetes.api.model.IntOrStringBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressPathBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressRuleValueBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressBackendBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressRuleBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;
import info.scce.cincocloud.db.PyroProjectDB;

public class PyroAppK8SIngressFrontend extends PyroK8SResource<Ingress> {

    private final PyroAppK8SService service;
    private final String host;

    public PyroAppK8SIngressFrontend(KubernetesClient client, PyroAppK8SService service, PyroProjectDB project, String host) {
        super(client, project);
        this.service = service;
        this.host = host;
        this.resource = build();
    }

    @Override
    protected Ingress build() {
        final var path = getPath().substring(0, getPath().length() - 1) + "(/|$)(.*)";

        return new IngressBuilder()
                .withNewMetadata()
                .withName(getProjectName() + "-app-ingress-frontend")
                .withAnnotations(Map.of(
                        "nginx.ingress.kubernetes.io/rewrite-target", "/$2",
                        "nginx.ingress.kubernetes.io/use-regex", "true"))
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
                                                            .withServiceName(service.getResource().getMetadata().getName())
                                                            .withServicePort(new IntOrStringBuilder()
                                                                    .withIntVal(service.getFrontendPort())
                                                                    .build())
                                                            .build())
                                                    .build()
                                        )
                                        .build())
                                .build())
                        .build())
                .build();
    }

    public String getPath() {
        return "/workspaces/" + getProjectName() + "/theia/" ;
    }
}
