package info.scce.cincocloud.k8s;

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

public class ProjectK8SIngress extends ProjectK8SResource<Ingress> {

    private final ProjectK8SService service;

    public ProjectK8SIngress(KubernetesClient client, ProjectK8SService service, PyroProjectDB project) {
        super(client, project);
        this.service = service;
        this.resource = build();
    }

    /**
     * Equivalent to:
     *
     * apiVersion: networking.k8s.io/v1
     * kind: Ingress
     * metadata:
     *   name: {name}-ingress
     *   annotations:
     *     nginx.ingress.kubernetes.io/add-base-url: "true"
     *     nginx.ingress.kubernetes.io/rewrite-target: /$2
     * spec:
     *   rules:
     *     - host: cinco-cloud
     *       http:
     *         paths:
     *           - path: /workspaces/{name}
     *             pathType: Prefix
     *             backend:
     *               service:
     *                 name: {name}-service
     *                 port:
     *                   name: {name}-port
     *
     * @return the ingress controller.
     */
    @Override
    protected Ingress build() {
        return new IngressBuilder()
                .withNewMetadata()
                    .withName(getProjectName() + "-ingress")
                    .withAnnotations(Map.of(
                            "nginx.ingress.kubernetes.io/add-base-url", "true",
                            "nginx.ingress.kubernetes.io/rewrite-target", "/$2"))
                .endMetadata()
                .withSpec(new IngressSpecBuilder()
                        .withRules(new IngressRuleBuilder()
                                .withHost("cinco-cloud")
                                .withHttp(new HTTPIngressRuleValueBuilder()
                                        .withPaths(new HTTPIngressPathBuilder()
                                                .withPath("/workspaces/" + getProjectName())
                                                .withPathType("Prefix")
                                                .withBackend(new IngressBackendBuilder()
                                                        .withServiceName(service.getResource().getMetadata().getName())
                                                        .withServicePort(new IntOrStringBuilder()
                                                                .withStrVal(service.getResource().getSpec().getPorts().get(0).getName())
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
