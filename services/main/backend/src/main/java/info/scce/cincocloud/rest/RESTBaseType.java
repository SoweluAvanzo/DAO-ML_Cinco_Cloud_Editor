package info.scce.cincocloud.rest;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import info.scce.cincocloud.util.Constants;

@JsonIdentityInfo(generator = JSOGGenerator.class)
public interface RESTBaseType {

  @JsonProperty(Constants.CINCO_CLOUD_ID_PROPERTY_NAME)
  long getId();

  @JsonProperty(Constants.CINCO_CLOUD_ID_PROPERTY_NAME)
  void setId(final long id);
}
