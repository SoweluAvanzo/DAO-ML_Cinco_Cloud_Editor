package entity.core;


public class PyroUserDB {
    
	public long id;
    public String username;
    public String email;
    public String profilePicture; 
    
    PyroUserDB(String raw) {
    	String[] content = raw.split(";");
    	id = Long.parseLong(content[0]);
    	username = content[0];
    	email = content[0];
    	profilePicture = content[0];
    }

    public static PyroUserDB getCurrentUser(javax.ws.rs.core.SecurityContext context) {
        PyroUserDB user = new PyroUserDB(context.getUserPrincipal().getName());
    	return user;
    }
}

