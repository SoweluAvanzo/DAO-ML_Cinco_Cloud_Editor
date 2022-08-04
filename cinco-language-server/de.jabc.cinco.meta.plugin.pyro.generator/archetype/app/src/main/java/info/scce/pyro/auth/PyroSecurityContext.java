package info.scce.pyro.auth;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

public class PyroSecurityContext implements SecurityContext {

	final AuthPojo user;
	
	public PyroSecurityContext(AuthPojo user) {
		this.user = user;
		PyroUserRegistry.addUser(user);
	}
	
    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
        	@Override
            public String getName() {
            	return user != null ? user.getUserString() : null;
            }
        };
    }
    
    @Override
    public boolean isUserInRole(String r) {
        return user != null;
    }

    @Override
    public boolean isSecure() {
        return user != null;
    }

    @Override
    public String getAuthenticationScheme() {
        return "basic";
    }
}
