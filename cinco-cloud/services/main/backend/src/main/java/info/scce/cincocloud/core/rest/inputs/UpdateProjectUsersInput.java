package info.scce.cincocloud.core.rest.inputs;

import jakarta.validation.constraints.NotNull;

public class UpdateProjectUsersInput {

  @NotNull(message = "Field 'userId' may not be null")
  private long userId;

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getUserId() {
    return userId;
  }
}
