package entity.core;

public class PyroUserDB {
    
	public long id;
    public String username;
    public String email;
    public String profilePicture; 
    
    public PyroUserDB(String raw) {
    	String[] content = raw.split(";");
    	id = Long.parseLong(content[0]);
    	username = content[1];
    	email = content[2];
    	profilePicture = content[3];
    }

    public static PyroUserDB getCurrentUser(javax.ws.rs.core.SecurityContext context) {
        String userString = context.getUserPrincipal().getName();
        PyroUserDB user = new PyroUserDB(userString);
    	return user;
    }
}

