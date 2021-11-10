package info.scce.cincocloud.rest;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import info.scce.cincocloud.util.Constants;

@JsonIdentityInfo(generator = JSOGGenerator.class)
public interface RESTBaseType {

    @JsonProperty(Constants.PYRO_ID)
    long getId();

    @JsonProperty(Constants.PYRO_ID)
    void setId(final long id);
}