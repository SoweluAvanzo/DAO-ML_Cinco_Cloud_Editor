package info.scce.cincocloud;

public class AbortBuildJobMessage {

  /**
   * The ID of the build job to abort.
   */
  public Long jobId;

  @Override
  public String toString() {
    return "AbortBuildJobMessage{"
        + "jobId=" + jobId
        + '}';
  }
}
