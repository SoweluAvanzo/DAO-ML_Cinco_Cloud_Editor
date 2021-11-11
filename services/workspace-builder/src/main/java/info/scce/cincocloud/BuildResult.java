package info.scce.cincocloud;

public class BuildResult {

  /**
   * The ID of the workspace. Should match with the ID of the corresponding build job {@link BuildJob}.
   */
  public Long projectId;

  /**
   * The ID of the job.
   */
  public Long jobId;

  /**
   * The the image could be built successfully.
   */
  public Boolean success;

  /**
   * A message that contains e.g. an error message if {@link #success} is false.
   */
  public String message;

  /**
   * The name of the built image.
   */
  public String image;

  public BuildResult(Long projectId, Long jobId, Boolean success, String message, String image) {
    this.projectId = projectId;
    this.jobId = jobId;
    this.success = success;
    this.message = message;
    this.image = image;
  }

  @Override
  public String toString() {
    return "JobResult{"
        + "projectId=" + projectId
        + ", jobId=" + jobId
        + ", success=" + success
        + ", message='" + message + '\''
        + ", image='" + image + '\''
        + '}';
  }
}
