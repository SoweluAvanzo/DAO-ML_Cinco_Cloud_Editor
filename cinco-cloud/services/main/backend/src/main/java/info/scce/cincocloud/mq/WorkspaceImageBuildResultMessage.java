package info.scce.cincocloud.mq;

import java.util.UUID;

public class WorkspaceImageBuildResultMessage {

  /**
   * The ID of the project. Should match with the ID of the corresponding build job {@link
   * WorkspaceImageBuildJobMessage}.
   */
  public Long projectId;

  /**
   * The ID of the job.
   */
  public Long jobId;

  /**
   * The image could be built successfully.
   */
  public Boolean success;

  /**
   * A message that contains e.g. an error message if {@link #success} is false.
   */
  public String message;

  /**
   * The name of the built image.
   */
  public UUID uuid;
}
