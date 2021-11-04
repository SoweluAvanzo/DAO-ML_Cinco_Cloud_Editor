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
        String raw = context.getUserPrincipal().getName();
        PyroUserDB user = new PyroUserDB(raw);
    	return user;
    }
}

