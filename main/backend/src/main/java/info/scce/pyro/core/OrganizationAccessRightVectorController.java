package info.scce.pyro.core;


import javax.ws.rs.core.SecurityContext;
import info.scce.pyro.core.rest.types.PyroOrganizationAccessRightVector;
	
@javax.transaction.Transactional
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Path("/organization/{orgId}/accessRights")
@javax.enterprise.context.RequestScoped
public class OrganizationAccessRightVectorController {
	
		
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response getAll(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);
			if (org == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (isOwnerOf(subject, org)) {
				final java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> result = entity.core.PyroOrganizationAccessRightVectorDB.listAll();
				
				final java.util.List<PyroOrganizationAccessRightVector> arvs = new java.util.ArrayList<>();
				for (entity.core.PyroOrganizationAccessRightVectorDB arv: result) {
					arvs.add(PyroOrganizationAccessRightVector.fromEntity(arv, objectCache));
				}
				
				return javax.ws.rs.core.Response.ok(arvs).build();
			}
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/my")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response get(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);
			if (org == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			final java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> result = entity.core.PyroOrganizationAccessRightVectorDB.list("user = ?1 and organization = ?2",subject,org);
			if (result.size() == 1) {
				return javax.ws.rs.core.Response.ok(PyroOrganizationAccessRightVector.fromEntity(result.get(0), objectCache)).build();
			}					
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.PUT
	@javax.ws.rs.Path("/{arvId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response update(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("orgId") final long orgId, 
			@javax.ws.rs.PathParam("arvId") final long arvId,
			final PyroOrganizationAccessRightVector arv) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);
			if (org == null) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			final entity.core.PyroOrganizationAccessRightVectorDB arvInDb = entity.core.PyroOrganizationAccessRightVectorDB.findById(arvId);
			if (arvInDb == null) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			arvInDb.accessRights.clear();
			arvInDb.accessRights.addAll(arv.getaccessRights());
			arvInDb.persist();
			
			return javax.ws.rs.core.Response.ok(PyroOrganizationAccessRightVector.fromEntity(arvInDb, objectCache)).build();					
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	private boolean isOwnerOf(entity.core.PyroUserDB user, entity.core.PyroOrganizationDB org) {
		return org.owners.contains(user);
	}
}	
