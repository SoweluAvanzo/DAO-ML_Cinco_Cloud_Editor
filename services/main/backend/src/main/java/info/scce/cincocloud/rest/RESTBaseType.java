package info.scce.cincocloud.rest;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.util.Constants;

@JsonIdentityInfo(generator = com.voodoodyne.jackson.jsog.JSOGGenerator.class)
public interface RESTBaseType {

    @JsonProperty(Constants.PYRO_ID)
    long getId();

    @JsonProperty(Constants.PYRO_ID)
    void setId(final long id);
}