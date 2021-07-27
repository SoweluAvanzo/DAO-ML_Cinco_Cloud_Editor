package info.scce.pyro.auth;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
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
        String auth = requestContext.getHeaders().getFirst("Authorization");
        if (auth != null) {
            final AuthPojo a = authClient.getUser(auth);
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

    }
}