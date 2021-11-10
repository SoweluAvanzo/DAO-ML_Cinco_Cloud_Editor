package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class K8SResource<T extends HasMetadata> {

    protected T resource;
    protected KubernetesClient client;

    public K8SResource(KubernetesClient client) {
        this.client = client;
    }

    public T getResource() {
        return resource;
    }
}
