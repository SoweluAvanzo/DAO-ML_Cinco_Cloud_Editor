package info.scce.cincocloud.k8s;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class K8SIngressService {

  @ConfigProperty(name = "quarkus.http.root-path")
  String httpRootPath;

  public String getWorkspaceRootPath() {
    var root = httpRootPath == null || httpRootPath.isBlank() || httpRootPath.equals("/") ? "" : httpRootPath;
    return root + "/workspaces";
  }
}
