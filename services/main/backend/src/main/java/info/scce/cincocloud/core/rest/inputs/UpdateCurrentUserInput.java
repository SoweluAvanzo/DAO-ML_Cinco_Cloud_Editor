package info.scce.cincocloud.core.rest.inputs;

import info.scce.cincocloud.core.rest.tos.FileReferenceTO;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UpdateCurrentUserInput {

  public FileReferenceTO profilePicture;

  @NotEmpty(message = "The name may not be empty.")
  public String name;

  @NotEmpty(message = "The email may not be empty.")
  @Email(message = "The value is not a valid email.")
  public String email;
}
