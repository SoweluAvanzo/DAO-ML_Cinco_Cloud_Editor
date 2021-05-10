package info.scce.pyro.core.rest;

import info.scce.pyro.core.rest.types.PyroUserRegistration;
import javax.ws.rs.core.SecurityContext;
import info.scce.pyro.auth.PBKDF2Encoder;
import info.scce.pyro.core.rest.types.FindPyroUser;
import info.scce.pyro.core.OrganizationController;
import info.scce.pyro.core.rest.types.PyroUser;

@javax.transaction.Transactional
@javax.ws.rs.Path("/register/")
public class RegistrationController {
	
	private static final int MIN_PASSWORD_LENGTH = 5;


	@javax.inject.Inject
	PBKDF2Encoder passwordEncoder;
	
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.inject.Inject
	info.scce.pyro.core.OrganizationController organizationController;
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("new/public")
	@javax.annotation.security.PermitAll
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response registerUser(@javax.ws.rs.core.Context SecurityContext securityContext,PyroUserRegistration pyroUserRegistration) {

		if(
			pyroUserRegistration.getemail()==null||
			pyroUserRegistration.getusername()==null||
			pyroUserRegistration.getname()==null||
			pyroUserRegistration.getpassword()==null
		) {
			return javax.ws.rs.core.Response.status(
				javax.ws.rs.core.Response.Status.FORBIDDEN
			).build();
		}
		
		if(
			pyroUserRegistration.getemail().isEmpty()||
			pyroUserRegistration.getusername().isEmpty()||
			pyroUserRegistration.getname().isEmpty()||
			pyroUserRegistration.getpassword().isEmpty()
		) {
			return javax.ws.rs.core.Response.status(
				javax.ws.rs.core.Response.Status.FORBIDDEN
			).build();
		}

		if(pyroUserRegistration.getpassword().length() < MIN_PASSWORD_LENGTH){
			return javax.ws.rs.core.Response.status(
				javax.ws.rs.core.Response.Status.FORBIDDEN
			).build();
		}

		final java.util.List<entity.core.PyroUserDB> users = entity.core.PyroUserDB.list("email",pyroUserRegistration.getemail());
		
		boolean userShouldBeAdmin = entity.core.PyroUserDB.count() <=0;
		if(users.isEmpty()){
			entity.core.PyroUserDB user = entity.core.PyroUserDB.add(
				pyroUserRegistration.getemail(),
				pyroUserRegistration.getusername(),
				passwordEncoder.encode(pyroUserRegistration.getpassword())
			);
			if (userShouldBeAdmin) {
				user.systemRoles.add(entity.core.PyroSystemRoleDB.ADMIN);
				user.systemRoles.add(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER);
			}
			user.persist();
				
			// TODO: SAMI: send activation mail
			user.isActivated = true; // TODO: SAMI: remove this later (for development use)

			return javax.ws.rs.core.Response.ok("Activation mail send").build();
		}

		return javax.ws.rs.core.Response.status(
				javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
}

