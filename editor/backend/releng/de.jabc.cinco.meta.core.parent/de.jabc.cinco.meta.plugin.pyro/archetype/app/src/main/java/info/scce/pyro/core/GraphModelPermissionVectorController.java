package info.scce.pyro.core;


import info.scce.pyro.core.rest.types.*;

import javax.ws.rs.core.SecurityContext;
	
@javax.transaction.Transactional
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Path("/project/{projectId}/graphModelPermissions")
@javax.enterprise.context.RequestScoped
public class GraphModelPermissionVectorController {
	
	
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response getAll(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("projectId") final long projectId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			if (project == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (mayAccess(subject, project)) {
				final java.util.List<entity.core.PyroGraphModelPermissionVectorDB> result = entity.core.PyroGraphModelPermissionVectorDB.find("project",project).list();
				
				final java.util.List<PyroGraphModelPermissionVector> permissions = new java.util.ArrayList<>();
				for (entity.core.PyroGraphModelPermissionVectorDB permission: result) {
					permissions.add(PyroGraphModelPermissionVector.fromEntity(permission, objectCache));
				}
				
				return javax.ws.rs.core.Response.ok(permissions).build();
			}
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/my")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response get(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("projectId") final long projectId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			if (project == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			final java.util.List<entity.core.PyroGraphModelPermissionVectorDB> result = entity.core.PyroGraphModelPermissionVectorDB.find("user = ?1 and project = ?2",subject,project).list();
			final java.util.List<PyroGraphModelPermissionVector> permissions = new java.util.ArrayList<>();
			for (entity.core.PyroGraphModelPermissionVectorDB permission: result) {
				permissions.add(PyroGraphModelPermissionVector.fromEntity(permission, objectCache));
			}
			
			return javax.ws.rs.core.Response.ok(permissions).build();				
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.PUT
	@javax.ws.rs.Path("/{permissionId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response update(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId, 
			@javax.ws.rs.PathParam("permissionId") final long permissionId,
			final PyroGraphModelPermissionVector permissionVector) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			if (project == null) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			if (mayAccess(subject, project)) {
				final entity.core.PyroGraphModelPermissionVectorDB permissionVectorInDb = entity.core.PyroGraphModelPermissionVectorDB.findById(permissionId);
				if (permissionVectorInDb == null) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
				
				permissionVectorInDb.permissions.clear();
				permissionVectorInDb.permissions.addAll(permissionVector.getpermissions());
				permissionVectorInDb.persist();
				
				return javax.ws.rs.core.Response.ok(PyroGraphModelPermissionVector.fromEntity(permissionVectorInDb, objectCache)).build();	
			}				
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	private boolean mayAccess(entity.core.PyroUserDB user, 
							  entity.core.PyroProjectDB project) {
		return user.systemRoles.contains(entity.core.PyroSystemRoleDB.ADMIN) 
				|| project.organization.owners.contains(user)
				|| project.owner.equals(user);	
	}
}	
