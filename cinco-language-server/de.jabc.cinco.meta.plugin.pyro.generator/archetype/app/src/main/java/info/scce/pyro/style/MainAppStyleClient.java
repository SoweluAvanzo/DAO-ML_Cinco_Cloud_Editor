package info.scce.pyro.style;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
/**
 * Author zweihoff
 */
@Path("/style")
@RegisterRestClient
public interface MainAppStyleClient {

    @GET
    @Path("/")
    StylePojo getStyle();
}
