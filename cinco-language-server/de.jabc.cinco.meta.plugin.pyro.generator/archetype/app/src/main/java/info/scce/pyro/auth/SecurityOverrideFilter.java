package info.scce.pyro.auth;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import org.jboss.logging.Logger;

@Provider
@PreMatching
public class SecurityOverrideFilter implements ContainerRequestFilter {
	private static final Logger logger = Logger.getLogger(SecurityOverrideFilter.class);
	
    @Inject
    @RestClient
    MainAppAuthClient authClient;
    public static boolean ACCEPT_NO_USER = false;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	MultivaluedMap<String, String> headers = requestContext.getHeaders();
    	AuthPojo user = getUser(headers);
        setUpSecurityContext(requestContext, user);
    }
    
    private AuthPojo getUser(MultivaluedMap<String, String> headers) {
    	if(isDebugging()) {
			logger.info("Debugging is on. Using DebugUser.");
        	return AuthPojo.getDebugUser();
    	}
        else {
	    	String jwtToken = headers.getFirst("Authorization");
	        if (jwtToken != null) {
				try {
					if(!jwtToken.contains("Bearer")) {
						jwtToken = "Bearer " + jwtToken;
					}
					logger.info("jwt token: "+jwtToken);
					return authClient.getUser(jwtToken, "application/json");
				} catch(Exception e) {
					e.printStackTrace();
					if(allowNoUser()) {
						logger.info("User couldn't be resolved from jwt (or service is not reachable).\nFalling back to no-user!");
						return AuthPojo.getNoUser();
					}
				}
	        }
			logger.error("no jwt defined! no user can be fetched!");
        	return null;
        }
    }
    
    private static void setUpSecurityContext(ContainerRequestContext requestContext, final AuthPojo user) {
    	requestContext.setSecurityContext(new PyroSecurityContext(user));
    }
    
    public static boolean isDebugging() {
    	try {
        	Map<String, String> env = System.getenv();
        	try {
        		// is debugging
        		return Boolean.parseBoolean(env.get("CINCO_CLOUD_DEBUG"));
        	} catch(Exception e) {}
    		return java.lang.management.ManagementFactory.getRuntimeMXBean().
    	         getInputArguments().toString().indexOf("-Ddebug") > 0;
    	} catch(Exception e) {
    		return false;
    	}
    }
    
    /*
     *  Set PYRO_ALLOW_NO_USER to "true",
     *  if you want to turn of debugging-mode,
     *  but the pyro-server is not connected to a main-service. 
     *  This way AuthPojo.getNoUser() will return a dummy "no"-user,
     *  that will be handled as the current user.
     */
    public static boolean allowNoUser() {
    	try {
        	Map<String, String> env = System.getenv();
        	try {
        		// is debugging
        		String allowNoUser = env.get("PYRO_ALLOW_NO_USER");
        		return Boolean.parseBoolean(allowNoUser);
        	} catch(Exception e) {}
    		return false;
    	} catch(Exception e) {
    		return false;
    	}
    }
    
    public static String getWorkspacePath() {
    	String workspace = System.getProperty("user.home") + "/editor/workspace/";
    	try {
        	Map<String, String> env = System.getenv();
    		String workspace_path = env.get("WORKSPACE_PATH");
    		if(workspace_path != null && !workspace_path.isEmpty()) {
    			logger.info("WORKSPACE_PATH defined:\n"+workspace_path);
        		return workspace_path.strip().replaceAll("'", "");
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	logger.info("No WORKSPACE_PATH defined. Falling back to default:\n"+workspace);
    	return workspace;
    }
}
