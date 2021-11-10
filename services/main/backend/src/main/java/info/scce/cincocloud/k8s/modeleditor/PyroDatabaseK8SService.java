package info.scce.cincocloud.k8s.modeleditor;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Map;
import info.scce.cincocloud.db.PyroProjectDB;

public class PyroDatabaseK8SService extends PyroK8SResource<Service> {

    public PyroDatabaseK8SService(KubernetesClient client, PyroProjectDB project) {
        super(client, project);
        this.resource = build();
    }

    @Override
    protected Service build() {
        return new ServiceBuilder()
                .withNewMetadata()
                    .withName(getProjectName() + "-database-service")
                    .withNamespace(client.getNamespace())
                    .withLabels(Map.of("app", getProjectName()))
                .endMetadata()
                .withSpec(new ServiceSpecBuilder()
                        .withPorts(new ServicePortBuilder()
                                .withPort(5432)
                                .withProtocol("TCP")
                                .build())
                        .withType("NodePort")
                        .withSelector(Map.of("app", getProjectName() + "-database-statefulset"))
                        .build())
                .build();
    }
}
