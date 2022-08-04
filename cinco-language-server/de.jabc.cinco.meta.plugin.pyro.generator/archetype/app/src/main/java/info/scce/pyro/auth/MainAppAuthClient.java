package info.scce.pyro.auth;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

/**
 * Author zweihoff
 */
@Path("/api/user/current")
@RegisterRestClient
public interface MainAppAuthClient {

    @GET
    @Path("private")
    AuthPojo getUser(@HeaderParam("Authorization") String token, @HeaderParam("Accept") String accept);
}
