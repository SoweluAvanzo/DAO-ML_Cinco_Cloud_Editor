package info.scce.cincocloud.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroUserRegistration {

    private String username;
    private String email;
    private String name;
    private String password;

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public String getusername() {
        return this.username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public void setusername(final String username) {
        this.username = username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public String getemail() {
        return this.email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public void setemail(final String email) {
        this.email = email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final String name) {
        this.name = name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("password")
    public String getpassword() {
        return this.password;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("password")
    public void setpassword(final String password) {
        this.password = password;
    }
}