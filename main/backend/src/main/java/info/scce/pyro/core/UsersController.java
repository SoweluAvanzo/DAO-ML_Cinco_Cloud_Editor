package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.PyroUser;
import info.scce.pyro.core.rest.types.PyroUserSearch;
import javax.ws.rs.core.SecurityContext;


@javax.ws.rs.Path("/users")
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.transaction.Transactional
@javax.enterprise.context.RequestScoped
public class UsersController {

    @javax.inject.Inject
    OrganizationController organizationController;
    
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	/** Get all users. */
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response  getUsers(@javax.ws.rs.core.Context SecurityContext securityContext) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
	
		if(subject!=null && isAdmin(subject)) {
			
			final java.util.List<entity.core.PyroUserDB> result = entity.core.PyroUserDB.listAll();
			
			final java.util.List<PyroUser> users = new java.util.ArrayList<>();
			for (entity.core.PyroUserDB user : result) {
				users.add(PyroUser.fromEntity(user, objectCache));
			}
 
			return javax.ws.rs.core.Response.ok(users).build();
		}

		return javax.ws.rs.core.Response.status(
				javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	/** Get a user by its username or email. */
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/search")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response searchUser(@javax.ws.rs.core.Context SecurityContext securityContext,PyroUserSearch search) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if(subject!=null && isAdmin(subject)) {
			
			final java.util.List<entity.core.PyroUserDB> resultByUsername = entity.core.PyroUserDB.list("username", search.getusernameOrEmail());
			if (resultByUsername.size() == 1) {
				return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(resultByUsername.get(0), objectCache)).build();
			}
			
			final java.util.List<entity.core.PyroUserDB> resultByEmail = entity.core.PyroUserDB.list("email", search.getusernameOrEmail());
			if (resultByEmail.size() == 1) {
				return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(resultByEmail.get(0), objectCache)).build();
			}
			
			return javax.ws.rs.core.Response.status(
					javax.ws.rs.core.Response.Status.NOT_FOUND).build();
		}
		
		return javax.ws.rs.core.Response.status(
				javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.DELETE
	@javax.ws.rs.Path("/{userId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response delete(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		if(subject!=null && isAdmin(subject)) {
			final entity.core.PyroUserDB userToDelete = entity.core.PyroUserDB.findById(userId);
			if (subject.equals(userToDelete)) { // an admin should not delete himself
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build(); 
			}
			deleteUser(userToDelete);
			return javax.ws.rs.core.Response.ok().build();
		}
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{userId}/roles/addAdmin")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response makeAdmin(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if(subject!=null && isAdmin(subject)) {
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.findById(userId);
			if (user == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (!user.systemRoles.contains(entity.core.PyroSystemRoleDB.ADMIN)) {
				user.systemRoles.add(entity.core.PyroSystemRoleDB.ADMIN);
				user.systemRoles.add(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER);
			}
			
			return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user,objectCache)).build();
		}
		
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{userId}/roles/removeAdmin")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response makeUser(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if(subject!=null && isAdmin(subject)) {
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.findById(userId);
			if (user == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
						
			// an admin should not remove his own admin rights
			if (isAdmin(user) && user.id == subject.id) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
			}
			
			user.systemRoles.remove(entity.core.PyroSystemRoleDB.ADMIN);
			return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user,objectCache)).build();
		}
		
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{userId}/roles/addOrgManager")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response addOrgManager(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if(subject!=null && isAdmin(subject)) {
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.findById(userId);
			if (user == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (!user.systemRoles.contains(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER)) {
				user.systemRoles.add(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER);
			}
			
			return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user,objectCache)).build();
		}
		
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{userId}/roles/removeOrgManager")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response removeOrgManager(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if(subject!=null && isAdmin(subject)) {
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.findById(userId);
			if (user == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
												
			user.systemRoles.remove(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER);
			return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user,objectCache)).build();
		}
		
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
			
	private boolean isAdmin(entity.core.PyroUserDB user) {
		return user.systemRoles.contains(entity.core.PyroSystemRoleDB.ADMIN);
	}
	
	public void deleteUser(entity.core.PyroUserDB user) {
		java.util.List<entity.core.PyroOrganizationDB> orgs = entity.core.PyroOrganizationDB.listAll();
		orgs.stream().forEach( (org) -> {
			if(org.owners.contains(user) || org.members.contains(user)) {
				this.organizationController.removeFromOrganization(user, org);
			}
		});
		user.delete();
	}
}
