package info.scce.cincocloud.core.rest.types;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PyroUserRegistration {

    @NotEmpty(message = "The username may not be empty.")
    private String username;

    @NotNull
    @Email
    private String email;

    @NotEmpty(message = "The name may not be empty.")
    private String name;

    @NotEmpty(message = "The password may not be empty.")
    @Size(min = 5, message = "The password has to be at least five characters long.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}