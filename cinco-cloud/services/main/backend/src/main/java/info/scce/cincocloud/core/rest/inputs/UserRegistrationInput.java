package info.scce.cincocloud.core.rest.inputs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserRegistrationInput {

  private static final int MIN_PASSWORD_LENGTH = 5;

  @NotEmpty(message = "The username may not be empty.")
  private String username;

  @NotEmpty
  @Email
  private String email;

  @NotEmpty(message = "The name may not be empty.")
  private String name;

  @NotEmpty(message = "The password may not be empty.")
  @Size(min = MIN_PASSWORD_LENGTH, message = "The password has to be at least five characters long.")
  private String password;

  @NotEmpty(message = "The password confirmation may not be empty.")
  @Size(min = MIN_PASSWORD_LENGTH, message = "The password confirmation has to be at least five characters long.")
  private String passwordConfirm;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordConfirm() {
    return passwordConfirm;
  }

  public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
  }
}
