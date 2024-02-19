package info.scce.cincocloud.core.rest.inputs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateProjectInput {

  public Long logoId;

  @NotNull
  @Size(min = 1, message = "Name should have at least 1 character.")
  public String name;
  public String description;
}
