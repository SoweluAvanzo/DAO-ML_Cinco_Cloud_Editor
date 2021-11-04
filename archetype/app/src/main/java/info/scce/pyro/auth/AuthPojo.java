package info.scce.pyro.auth;

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
    	debugUser.id = ""+getNextId(); // TODO: need better debug-case
    	debugUser.profile_image = "debug.png";
    	return debugUser;
    }

    private static long getNextId() {
    	long id = idCounter;
    	idCounter++;
    	return id;
    }
}