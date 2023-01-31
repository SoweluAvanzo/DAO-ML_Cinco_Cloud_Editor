package info.scce.cincocloud.core.rest.inputs;

import javax.validation.constraints.NotNull;

public class UpdateProjectTransferToUserInput {

  @NotNull(message = "Field 'userId' may not be null")
  private long userId;

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getUserId() {
    return userId;
  }

}
