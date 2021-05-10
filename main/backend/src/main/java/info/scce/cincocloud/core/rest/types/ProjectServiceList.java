package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.util.List;

/**
 * Author zweihoff
 */

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ProjectServiceList {
    private List<String> active = new java.util.LinkedList<>();
    private List<String> disabled = new java.util.LinkedList<>();

    @JsonProperty("active")
    public List<String> getActive() {
        return this.active;
    }

    @JsonProperty("active")
    public void setActive(final List<String> active) {
        this.active = active;
    }

    @JsonProperty("disabled")
    public List<String> getDisabled() {
        return this.disabled;
    }

    @JsonProperty("disabled")
    public void setDisabled(final List<String> disabled) {
        this.disabled = disabled;
    }

}
