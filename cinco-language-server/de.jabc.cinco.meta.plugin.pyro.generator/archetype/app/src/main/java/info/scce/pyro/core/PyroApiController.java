package info.scce.pyro.core;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@javax.transaction.Transactional
@Path("/")
@javax.enterprise.context.RequestScoped
public class PyroApiController {
	
	@javax.ws.rs.GET
    @javax.ws.rs.Path("/")
	@Produces(MediaType.TEXT_PLAIN)
    public Response isRunning() {
		  return Response.ok("Pyro Server is Running").build();
    }
}