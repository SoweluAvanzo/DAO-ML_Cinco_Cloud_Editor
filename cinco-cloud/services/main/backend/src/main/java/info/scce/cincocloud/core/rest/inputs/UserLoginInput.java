package info.scce.cincocloud.core.rest.inputs;

import jakarta.validation.constraints.NotEmpty;

public class UserLoginInput {

  @NotEmpty(message = "The email or username may not be empty.")
  public String emailOrUsername;

  @NotEmpty(message = "The password may not be empty.")
  public String password;

  public UserLoginInput() {
  }

  public UserLoginInput(String emailOrUsername, String password) {
    this.emailOrUsername = emailOrUsername;
    this.password = password;
  }
}
