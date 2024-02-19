package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.sync.ticket.TicketMessage;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import jakarta.annotation.security.RolesAllowed;

import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
