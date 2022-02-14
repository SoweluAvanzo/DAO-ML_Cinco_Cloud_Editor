package info.scce.cincocloud.mq;

import java.util.UUID;

public class WorkspaceImageBuildJobMessage {

  /**
   * The image UUID
   */
  public UUID uuid;

  /**
   * The ID of the project.
   */
  public Long projectId;

  /**
   * The ID of the build job.
   */
  public Long jobId;

  public WorkspaceImageBuildJobMessage(
      UUID uuid,
      Long projectId,
      Long jobId
  ) {
    this.uuid = uuid;
    this.projectId = projectId;
    this.jobId = jobId;
  }

  @Override
  public String toString() {
    return "WorkspaceImageBuildJobMessage{"
        + "uuid=" + uuid
        + ", projectId=" + projectId
        + ", jobId=" + jobId
        + '}';
  }
}
