package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import javax.enterprise.context.ApplicationScoped;

import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class K8SClientService {

  @ConfigProperty(name = "kubernetes.namespace")
  String namespace;

  public KubernetesClient createClient() {
    return new KubernetesClientBuilder()
            .withConfig(new ConfigBuilder()
                    .withNamespace(namespace)
                    .build())
            .build();
  }
}
