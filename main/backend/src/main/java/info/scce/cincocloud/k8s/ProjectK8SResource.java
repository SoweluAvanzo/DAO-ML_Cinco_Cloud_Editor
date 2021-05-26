package info.scce.cincocloud.k8s;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import info.scce.cincocloud.db.PyroProjectDB;

public abstract class ProjectK8SResource<T extends HasMetadata> extends K8SResource<T> {

    protected PyroProjectDB project;

    public ProjectK8SResource(KubernetesClient client, PyroProjectDB project) {
        super(client);
        this.project = project;
    }

    protected abstract T build();

    protected String getProjectName() {
        return "project-" + project.id;
    }
}
