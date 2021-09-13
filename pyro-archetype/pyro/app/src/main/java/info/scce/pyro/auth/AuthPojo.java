package info.scce.pyro.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Author zweihoff
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthPojo {
    public String username;
    public String email;
    public String id;
    public String profile_image;

    public static AuthPojo getDebugUser() {
    	AuthPojo debugUser = new AuthPojo();
    	debugUser.username = "peter";
    	debugUser.email = "peter@parker.com";
    	debugUser.id = "1";
    	debugUser.profile_image = "debug.png";
    	return debugUser;
    }
}