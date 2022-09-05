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
    
    private static long idCounter = 0; 

    public static AuthPojo getDebugUser() {
    	AuthPojo debugUser = new AuthPojo();
    	debugUser.username = "peter";
    	debugUser.email = "peter@parker.com";
    	debugUser.id = "0";
    	debugUser.profile_image = "debug.png";
    	return debugUser;
    }
    
    public static AuthPojo getNoUser() {
    	AuthPojo noUser = new AuthPojo();
    	noUser.username = "no-user";
    	noUser.email = "no-user@no-user.com";
    	noUser.id = "0";
    	noUser.profile_image = "no-user.png";
    	return noUser;
    }
    
    public String getUserString() {
        return id+";"+username+";"+email+";"+profile_image;
    }

    private static long getNextId() {
    	long id = idCounter;
    	idCounter++;
    	return id;
    }
}