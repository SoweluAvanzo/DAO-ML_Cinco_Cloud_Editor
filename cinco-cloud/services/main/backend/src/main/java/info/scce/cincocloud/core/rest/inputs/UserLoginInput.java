package info.scce.cincocloud.core.rest.inputs;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserLoginInput {

  @NotEmpty(message = "The email may not be empty.")
  @Email(message = "The value is not a valid email.")
  public String email;

  @NotEmpty(message = "The password may not be empty.")
  public String password;

  public UserLoginInput() {
  }

  public UserLoginInput(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
