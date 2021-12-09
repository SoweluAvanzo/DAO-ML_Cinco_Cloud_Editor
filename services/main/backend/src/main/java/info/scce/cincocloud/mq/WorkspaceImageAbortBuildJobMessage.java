package info.scce.cincocloud.mq;

public class WorkspaceImageAbortBuildJobMessage {

  /**
   * The ID of the job to abort.
   */
  public Long jobId;

  public WorkspaceImageAbortBuildJobMessage(Long jobId) {
    this.jobId = jobId;
  }

  @Override
  public String toString() {
    return "WorkspaceImageAbortBuildJobMessage{"
        + "jobId=" + jobId
        + '}';
  }
}
