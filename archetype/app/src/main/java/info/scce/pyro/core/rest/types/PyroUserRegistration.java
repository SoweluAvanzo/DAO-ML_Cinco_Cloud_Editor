package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroUserRegistration {

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

    private String name;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final String name) {
        this.name = name;
    }

    private String password;

    @com.fasterxml.jackson.annotation.JsonProperty("password")
    public String getpassword() {
        return this.password;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("password")
    public void setpassword(final String password) {
        System.out.println(password);
        this.password = password;
    }
}