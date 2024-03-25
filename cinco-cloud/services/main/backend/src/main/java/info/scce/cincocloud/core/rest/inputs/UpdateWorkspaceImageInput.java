package info.scce.cincocloud.core.rest.inputs;

import javax.validation.constraints.NotNull;

public class UpdateWorkspaceImageInput {

    @NotNull(message = "Field 'published' may not be null")
    public Boolean published;

    public Boolean featured;
}
