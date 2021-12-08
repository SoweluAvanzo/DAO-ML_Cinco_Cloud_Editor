package info.scce.cincocloud.core.rest.inputs;

import org.hibernate.validator.constraints.Length;

public class UpdateCurrentUserPasswordInput {

  @Length(min = 5, max = 255, message = "The old password must be between 5 and 255 characters long.")
  public String oldPassword;

  @Length(min = 5, max = 255, message = "The new password must be between 5 and 255 characters long.")
  public String newPassword;
}
