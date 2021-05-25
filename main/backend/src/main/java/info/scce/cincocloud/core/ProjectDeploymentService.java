package info.scce.cincocloud.core;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.qute.Template;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.sync.ProjectWebSocket;

@ApplicationScoped
@Transactional
public class ProjectDeploymentService {

    private static final String DEFAULT_NAMESPACE = "default";

    @Inject
    Template workspace;

    @Inject
    ProjectWebSocket projectWebSocket;

    private KubernetesClient kubernetesClient;

    public ProjectDeploymentService() {
        this.kubernetesClient = new DefaultKubernetesClient();
    }

    public void deploy(PyroProjectDB project) {
        final String resource = getWorkspaceResource(project);
        System.out.println(resource);
//        kubernetesClient.resource(resource).inNamespace(DEFAULT_NAMESPACE).createOrReplace();
    }

    public void stop(PyroProjectDB project) {
        final String resource = getWorkspaceResource(project);
        System.out.println(resource);
//        kubernetesClient.resource(resource).inNamespace(DEFAULT_NAMESPACE).delete();
    }

    private String getWorkspaceResource(PyroProjectDB project) {
        return workspace.data("name", "project-" + project.id).render();
    }
}
