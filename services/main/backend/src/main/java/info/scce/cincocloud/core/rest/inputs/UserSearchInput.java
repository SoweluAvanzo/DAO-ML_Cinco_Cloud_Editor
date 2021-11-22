package info.scce.cincocloud.core.rest.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSearchInput {

  private String usernameOrEmail;

  @JsonProperty("usernameOrEmail")
  public String getusernameOrEmail() {
    return this.usernameOrEmail;
  }

  @JsonProperty("usernameOrEmail")
  public void setusernameOrEmail(final String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
  }
}
