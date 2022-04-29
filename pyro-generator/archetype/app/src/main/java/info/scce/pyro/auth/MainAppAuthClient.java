package info.scce.pyro.auth;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
/**
 * Author zweihoff
 */
@Path("/auth2")
@RegisterRestClient
public interface MainAppAuthClient {

    @GET
    @Path("/")
    AuthPojo getUser(@QueryParam("token") String token);
}
