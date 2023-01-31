package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.sync.ticket.TicketMessage;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class TicketController {

  @POST
  @RolesAllowed("user")
  public Response requestTicket(@Context SecurityContext securityContext) {
    final var subject = UserService.getCurrentUser(securityContext);

    TicketMessage ticket = TicketRegistrationHandler.createTicket(subject);

    return Response.ok(ticket).build();
  }
}
