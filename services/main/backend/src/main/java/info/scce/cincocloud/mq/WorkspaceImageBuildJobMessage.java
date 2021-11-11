package info.scce.cincocloud.mq;

public class WorkspaceImageBuildJobMessage {

  /**
   * The ID of the project.
   */
  public Long projectId;

  /**
   * The ID of the build job.
   */
  public Long jobId;

  /**
   * The username the workspace belongs to.
   */
  public String username;

  /**
   * The name of the language for which a workspace image should be build.
   */
  public String language;

  public WorkspaceImageBuildJobMessage(Long projectId, Long jobId, String username,
      String language) {
    this.projectId = projectId;
    this.jobId = jobId;
    this.username = username;
    this.language = language;
  }

  @Override
  public String toString() {
    return "WorkspaceImageBuildJobMessage{"
        + "projectId=" + projectId
        + ", jobId=" + jobId
        + ", username='" + username + '\''
        + ", language='" + language + '\''
        + '}';
  }
}
