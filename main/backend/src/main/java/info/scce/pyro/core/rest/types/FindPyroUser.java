package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class FindPyroUser {

    private String username;

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public String getusername() {
        return this.username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public void setusername(final String username) {
        this.username = username;
    }

    private String email;

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public String getemail() {
        return this.email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public void setemail(final String email) {
        this.email = email;
    }
}