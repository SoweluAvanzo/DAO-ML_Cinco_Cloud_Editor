package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PyroUserSearch {

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