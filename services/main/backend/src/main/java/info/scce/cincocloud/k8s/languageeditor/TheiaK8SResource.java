package info.scce.cincocloud.k8s.languageeditor;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.k8s.K8SResource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class TheiaK8SResource<T extends HasMetadata> extends K8SResource<T> {

  protected ProjectDB project;

  public TheiaK8SResource(KubernetesClient client, ProjectDB project) {
    super(client);
    this.project = project;
  }

  protected abstract T build();

  protected String getProjectName() {
    return "project-" + project.id;
  }
}
