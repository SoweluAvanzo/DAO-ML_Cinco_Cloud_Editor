package info.scce.cincocloud.core;

import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.sync.ticket.TicketMessage;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Path("/ticket")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketController {

  @GET
  @Path("/")
  @RolesAllowed("user")
  public Response requestTicket(@Context SecurityContext securityContext) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    if (subject == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    TicketMessage ticket = TicketRegistrationHandler.createTicket(subject);
    return Response.ok(ticket).build();
  }
}
