package info.scce.cincocloud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class BuildJob {

    /**
     * The ID of the workspace.
     */
    @NotNull(message = "The ID must not be null.")
    @Min(value = 0, message = "The ID must not be < 0.")
    public Long projectId;

    /**
     * The username the workspace belongs to.
     */
    @NotBlank(message = "The username must not be empty.")
    public String username;

    /**
     * The name of the language for which a workspace image should be build.
     */
    @NotBlank(message = "The language must not be empty.")
    public String language;

    @JsonIgnore
    public String getImageTag() {
        return username + "/" + language + "-" + projectId + ":latest";
    }
}
