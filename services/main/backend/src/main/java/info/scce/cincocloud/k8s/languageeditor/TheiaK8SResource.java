package info.scce.cincocloud.k8s.languageeditor;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.k8s.K8SResource;

public abstract class TheiaK8SResource<T extends HasMetadata> extends K8SResource<T> {

    protected PyroProjectDB project;

    public TheiaK8SResource(KubernetesClient client, PyroProjectDB project) {
        super(client);
        this.project = project;
    }

    protected abstract T build();

    protected String getProjectName() {
        return "project-" + project.id;
    }
}
