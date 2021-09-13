package info.scce.pyro.auth;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Provider
@PreMatching
public class SecurityOverrideFilter implements ContainerRequestFilter {

    @Inject
    @RestClient
    MainAppAuthClient authClient;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	// Debugging case (ignore security-token)
        if(isDebugging()) {
        	final AuthPojo a = AuthPojo.getDebugUser();
            setUpSecurityContext(requestContext, a);
            return;
    	}
    	MultivaluedMap<String, String> headers = requestContext.getHeaders();
        String auth = headers.getFirst("Authorization");
        if (auth != null) {
        	final AuthPojo a = authClient.getUser(auth);
            setUpSecurityContext(requestContext, a);
        }
    }
    
    private static void setUpSecurityContext(ContainerRequestContext requestContext, final AuthPojo a) {
    	if(a != null) {
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return a.id+";"+a.username+";"+a.email+";"+a.profile_image;
                        }
                    };
                }
                
                @Override
                public boolean isUserInRole(String r) {
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return true;
                }

                @Override
                public String getAuthenticationScheme() {
                    return "basic";
                }
            });
        }
    }
    
    public static boolean isDebugging() {
    	try {
    	return java.lang.management.ManagementFactory.getRuntimeMXBean().
    	         getInputArguments().toString().indexOf("-Ddebug") > 0;
    	} catch(Exception e) {
    		return false;
    	}
    }
}