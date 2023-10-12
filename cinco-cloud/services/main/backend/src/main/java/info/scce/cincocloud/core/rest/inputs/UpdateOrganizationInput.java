package info.scce.cincocloud.core.rest.inputs;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateOrganizationInput {
    public Long logoId;

    @NotNull
    @Size(min = 1, message = "Name should have at least 1 character.")
    public String name;
    public String description;
}
