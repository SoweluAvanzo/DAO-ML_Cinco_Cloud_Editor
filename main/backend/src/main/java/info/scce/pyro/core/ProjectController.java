package info.scce.pyro.core;

import entity.core.*;
import info.scce.pyro.core.rest.types.PyroProject;
import info.scce.pyro.core.rest.types.PyroProjectStructure;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.transaction.Transactional
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Path("/project")
@javax.enterprise.context.RequestScoped
public class ProjectController {

    @javax.inject.Inject
    private GraphModelController graphModelController;

    @javax.inject.Inject
	private ProjectService projectService;
    
	@javax.inject.Inject
	private info.scce.pyro.rest.ObjectCache objectCache;
		
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/create/private")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response createProject(@javax.ws.rs.core.Context SecurityContext securityContext,PyroProject newProject) {

		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if (newProject.getorganization() == null) {
			return javax.ws.rs.core.Response.status(Response.Status.BAD_REQUEST).build(); 
		}
		
		final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(newProject.getorganization().getId());
		if (org == null) {
			return javax.ws.rs.core.Response.status(Response.Status.NOT_FOUND).build(); 
		}
	
		if (canCreateProject(subject, org)) {
			final entity.core.PyroProjectDB pp = createProject(
				newProject.getname(),
				newProject.getdescription(),
				subject,
				org
			);
	        return javax.ws.rs.core.Response.ok(PyroProject.fromEntity(pp,objectCache)).build();
		}
		return javax.ws.rs.core.Response.status(Response.Status.FORBIDDEN).build(); 
	}
	
	public entity.core.PyroProjectDB createProject(
		String name,
		String description,
		entity.core.PyroUserDB subject,
		entity.core.PyroOrganizationDB org
	) {
		final entity.core.PyroProjectDB pp = new entity.core.PyroProjectDB();
		pp.owner = subject;
		pp.name = name;
		pp.description = description;
        pp.organization = org;
		subject.ownedProjects.add(pp);
        org.projects.add(pp);
        
        projectService.createDefaultGraphModelPermissionVectors(pp);
        projectService.createDefaultEditorGrid(pp);

        pp.persist();
        subject.persist();
        org.persist();
        
		
		return pp;
	}
	
    @javax.ws.rs.POST
	@javax.ws.rs.Path("/update/private")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response updateProject(@javax.ws.rs.core.Context SecurityContext securityContext,PyroProject ownedProject) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		final entity.core.PyroProjectDB pp = entity.core.PyroProjectDB.findById(ownedProject.getId());
		graphModelController.checkPermission(pp,securityContext);
		
		if(canEditProject(subject, pp)){
			pp.description = ownedProject.getdescription();
			pp.name = ownedProject.getname();
			
			// set new owner
			if (pp.organization.owners.contains(subject) 
				|| subject.systemRoles.size() > 0
				|| pp.owner.equals(subject)
			) {
				final entity.core.PyroUserDB newOwner = entity.core.PyroUserDB.findById(ownedProject.getowner().getId());
				if (newOwner == null) return javax.ws.rs.core.Response.status(Response.Status.NOT_FOUND).build();
				if (!isInOrganization(newOwner, pp.organization)) return javax.ws.rs.core.Response.status(Response.Status.BAD_REQUEST).build();               
				pp.owner.ownedProjects.remove(pp);
				pp.owner = newOwner;
				pp.persist();
				newOwner.ownedProjects.add(pp);
				newOwner.persist();
			}
			pp.persist();
			return javax.ws.rs.core.Response.ok(PyroProject.fromEntity(pp,objectCache)).build();
		}
		return javax.ws.rs.core.Response.status(Response.Status.FORBIDDEN).build(); 
	}
    
    @javax.ws.rs.GET
	@javax.ws.rs.Path("/{projectId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response getProject(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("projectId") final long projectId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
		
		graphModelController.checkPermission(project,securityContext);
		if(isInOrganization(subject, project.organization)){
			return javax.ws.rs.core.Response.ok(PyroProject.fromEntity(project, objectCache)).build();
		}
		return javax.ws.rs.core.Response.status(Response.Status.FORBIDDEN).build();
	}

    @javax.ws.rs.GET
	@javax.ws.rs.Path("/structure/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response loadProjectStructure(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
	
	    final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
	
	    final entity.core.PyroProjectDB pp = entity.core.PyroProjectDB.findById(id);
	
	    graphModelController.checkPermission(pp,securityContext);
	   
	    if(isInOrganization(subject, pp.organization)){
	        return javax.ws.rs.core.Response.ok(PyroProjectStructure.fromEntity(pp,objectCache)).build();
	    }
	    
	    return javax.ws.rs.core.Response.status(Response.Status.FORBIDDEN).build();
	}
	
    @javax.ws.rs.GET
	@javax.ws.rs.Path("/remove/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response removeProject(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(id);
		if (canDeleteProject(subject, project)) {		        	
			projectService.deleteById(subject, id, securityContext);
			return javax.ws.rs.core.Response.ok("Removed").build();
		}
		return javax.ws.rs.core.Response.status(Response.Status.FORBIDDEN).build();   
	}
	
    private boolean isInOrganization(
			entity.core.PyroUserDB user,
			entity.core.PyroOrganizationDB org) {
		return org.members.contains(user) || org.owners.contains(user);
	}

    private boolean canCreateProject(
			entity.core.PyroUserDB user,
			entity.core.PyroOrganizationDB org) {
		entity.core.PyroOrganizationAccessRightVectorDB arv = getAccessRightVector(user, org);
		return arv != null && arv.accessRights.contains(PyroOrganizationAccessRightDB.CREATE_PROJECTS);
	};
    
    private boolean canEditProject(
			entity.core.PyroUserDB user,
			entity.core.PyroProjectDB project) {
		entity.core.PyroOrganizationAccessRightVectorDB arv = getAccessRightVector(user, project);
		return arv != null && arv.accessRights.contains(PyroOrganizationAccessRightDB.EDIT_PROJECTS);
	};
    
    private boolean canDeleteProject(
			entity.core.PyroUserDB user,
			entity.core.PyroProjectDB project) {
		entity.core.PyroOrganizationAccessRightVectorDB arv = getAccessRightVector(user, project);
		return arv != null && arv.accessRights.contains(PyroOrganizationAccessRightDB.DELETE_PROJECTS);
	};
    
    private entity.core.PyroOrganizationAccessRightVectorDB getAccessRightVector(
			entity.core.PyroUserDB user,
			entity.core.PyroProjectDB project
			) {
		return getAccessRightVector(user, project.organization);
	}
    
    private entity.core.PyroOrganizationAccessRightVectorDB getAccessRightVector(
			entity.core.PyroUserDB user,
			entity.core.PyroOrganizationDB org
			) {
		final java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> result = entity.core.PyroOrganizationAccessRightVectorDB.list("user = ?1 and organization = ?2",user,org);
		return result.size() == 1 ? result.get(0) : null;
	}
}
