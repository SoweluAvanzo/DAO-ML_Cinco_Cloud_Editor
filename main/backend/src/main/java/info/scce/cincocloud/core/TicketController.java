package info.scce.cincocloud.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.sync.ticket.TicketMessage;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;

@javax.transaction.Transactional
@javax.ws.rs.Path("/ticket")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class TicketController {

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    @javax.annotation.security.RolesAllowed("user")
    public Response requestTicket(@javax.ws.rs.core.Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        if (subject == null) {
            return javax.ws.rs.core.Response.status(Status.FORBIDDEN).build();
        }
        TicketMessage ticket = TicketRegistrationHandler.createTicket(subject);
        return javax.ws.rs.core.Response.ok(ticket).build();
    }
}

