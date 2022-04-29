package info.scce.cincocloud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BuildJob {

  /**
   * The image UUID
   */
  public UUID uuid;

  /**
   * The ID of the workspace.
   */
  @NotNull(message = "The ID must not be null.")
  @Min(value = 0, message = "The ID must not be < 0.")
  public Long projectId;

  @NotNull(message = "The ID must not be null.")
  @Min(value = 0, message = "The ID must not be < 0.")
  public Long jobId;

  @JsonIgnore
  public String getImageTag() {
    return uuid + ":latest";
  }
}
