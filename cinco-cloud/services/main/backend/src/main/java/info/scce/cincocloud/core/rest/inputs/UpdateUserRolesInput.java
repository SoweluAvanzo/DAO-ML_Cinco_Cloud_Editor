package info.scce.cincocloud.core.rest.inputs;

import javax.validation.constraints.NotNull;

public class UpdateUserRolesInput {

  @NotNull(message = "Field 'admin' may not be null")
  private boolean admin;

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public boolean getAdmin() {
    return this.admin;
  }
}
