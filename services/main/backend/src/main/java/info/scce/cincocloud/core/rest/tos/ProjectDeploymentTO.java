package info.scce.cincocloud.core.rest.tos;

import info.scce.cincocloud.rest.RESTBaseImpl;

public class ProjectDeploymentTO extends RESTBaseImpl {

  private String url;

  private ProjectDeploymentStatus status;

  public ProjectDeploymentTO() {
  }

  public ProjectDeploymentTO(final String url, final ProjectDeploymentStatus status) {
    this.url = url;
    this.status = status;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ProjectDeploymentStatus getStatus() {
    return status;
  }

  public void setStatus(ProjectDeploymentStatus status) {
    this.status = status;
  }
}
