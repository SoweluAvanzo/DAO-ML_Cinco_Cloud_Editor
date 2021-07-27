package info.scce.pyro.core;


import info.scce.pyro.core.rest.types.*;

import javax.ws.rs.core.SecurityContext;
	
@javax.transaction.Transactional
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Path("/graphModelPermissions")
@javax.enterprise.context.RequestScoped
public class GraphModelPermissionVectorController {
	
	
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response getAll(@javax.ws.rs.core.Context SecurityContext securityContext) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			
			final java.util.List<entity.core.PyroGraphModelPermissionVectorDB> result = entity.core.PyroGraphModelPermissionVectorDB.find("userId",subject.id).list();
			
			final java.util.List<PyroGraphModelPermissionVector> permissions = new java.util.ArrayList<>();
			for (entity.core.PyroGraphModelPermissionVectorDB permission: result) {
				permissions.add(PyroGraphModelPermissionVector.fromEntity(permission, objectCache));
			}
			
			return javax.ws.rs.core.Response.ok(permissions).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/my")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response get(@javax.ws.rs.core.Context SecurityContext securityContext) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			
			final java.util.List<entity.core.PyroGraphModelPermissionVectorDB> result = entity.core.PyroGraphModelPermissionVectorDB.find("userId",subject.id).list();
			final java.util.List<PyroGraphModelPermissionVector> permissions = new java.util.ArrayList<>();
			for (entity.core.PyroGraphModelPermissionVectorDB permission: result) {
				permissions.add(PyroGraphModelPermissionVector.fromEntity(permission, objectCache));
			}
			
			return javax.ws.rs.core.Response.ok(permissions).build();				
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	
	
}	
