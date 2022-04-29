package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.PyroUser;

import javax.ws.rs.core.SecurityContext;

@javax.transaction.Transactional
@javax.ws.rs.Path("/user/current")
@javax.enterprise.context.RequestScoped
public class CurrentUserController {

	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;

	@javax.ws.rs.GET
	@javax.ws.rs.Path("private")
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response getCurrentUser(@javax.ws.rs.core.Context SecurityContext securityContext) {
		// TODO: SAMI: THEIA - DOES NOT RETURN THE CORRECT USER, I GUESS
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);

		if(subject!=null) {
			return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(subject, objectCache)).build();
		}

		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
}
