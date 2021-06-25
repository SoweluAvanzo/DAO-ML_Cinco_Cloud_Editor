package info.scce.pyro.core.rest.types;

public class PyroUserSearch {
	
	private String usernameOrEmail;

    @com.fasterxml.jackson.annotation.JsonProperty("usernameOrEmail")
    public String getusernameOrEmail() {
        return this.usernameOrEmail;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("usernameOrEmail")
    public void setusernameOrEmail(final String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}