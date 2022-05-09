package info.scce.pyro.core;

import info.scce.pyro.sync.ticket.TicketMessage;
import info.scce.pyro.sync.ticket.TicketRegistrationHandler;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

@javax.transaction.Transactional
@javax.ws.rs.Path("/ticket")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class TicketController {

	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public Response requestTicket(@javax.ws.rs.core.Context SecurityContext securityContext) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		if(subject == null)
			return javax.ws.rs.core.Response.status(Status.FORBIDDEN).build();
		TicketMessage ticket = TicketRegistrationHandler.createTicket(subject);
		return javax.ws.rs.core.Response.ok(ticket).build();
	}
}

