package info.scce.pyro.core;

import javax.ws.rs.core.SecurityContext;
import info.scce.pyro.core.rest.types.PyroOrganization;
import info.scce.pyro.core.rest.types.PyroUser;
import info.scce.pyro.util.DefaultColors;

@javax.transaction.Transactional
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Path("/organization")
@javax.enterprise.context.RequestScoped
public class OrganizationController {
	
	@javax.inject.Inject
	ProjectService projectService;
	
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
				
	@javax.inject.Inject
	info.scce.pyro.core.ProjectController projectController;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response getAll(@javax.ws.rs.core.Context SecurityContext securityContext) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final java.util.List<entity.core.PyroOrganizationDB> result = entity.core.PyroOrganizationDB.listAll();
			
			final java.util.List<PyroOrganization> orgs = new java.util.LinkedList<>();
			if (isOrgManager(subject)) {
				for (entity.core.PyroOrganizationDB org : result) {
					orgs.add(PyroOrganization.fromEntity(org, objectCache));
				}
			} else {
				for (entity.core.PyroOrganizationDB org : result) {
					if (org.members.contains(subject) || org.owners.contains(subject)) {
						orgs.add(PyroOrganization.fromEntity(org, objectCache));
					}
				}
			}
			
			return javax.ws.rs.core.Response.ok(orgs).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/{orgId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response get(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);
			if (org == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (isMemberOf(subject, org) || isOwnerOf(subject, org) || isOrgManager(subject)) {
				return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(org, objectCache)).build();
			}
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}

	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{orgId}/leave")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response leave(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);
		if (subject != null) {
			if(!removeFromOrganization(subject, org))
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
			org.persist();
			subject.persist();
			
			return javax.ws.rs.core.Response.ok().build();
		}
		
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{orgId}/addMember")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response addMember(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId, PyroUser user) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);
		
		if (subject != null && (isOrgManager(subject) || isOwnerOf(subject, org))) {
			final entity.core.PyroUserDB member =  entity.core.PyroUserDB.findById(user.getId());
			
			if (org == null || member == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (org.owners.contains(member)) {
				if (org.owners.size() == 1) { // do not make the ownly owner a member of the org
					return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
				}
				org.owners.remove(member);
			}
			
			if (!accessRightVectorExists(member, org)) {
				createDefaultAccessRightVector(member, org);
			}
			
			org.members.add(member);
			org.persist();
			return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(org, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{orgId}/addOwner")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response addOwner(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId, PyroUser user) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);	
		
		if (subject != null && (isOrgManager(subject) || isOwnerOf(subject, org))) {
			final entity.core.PyroUserDB owner = entity.core.PyroUserDB.findById(user.getId());
			
			if (org == null || owner == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (org.members.contains(owner)) {
				org.members.remove(owner);
				
			}
			
			if (!accessRightVectorExists(owner, org)) {
				createDefaultAccessRightVector(owner, org);
			}
			
			org.owners.add(owner);
			org.persist();
			return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(org, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}

	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{orgId}/removeUser")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response removeUser(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId, PyroUser user) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		final entity.core.PyroOrganizationDB org = entity.core.PyroOrganizationDB.findById(orgId);	
		
		if (subject != null && (isOrgManager(subject) || isOwnerOf(subject, org))) {
			final entity.core.PyroUserDB userToRemove = entity.core.PyroUserDB.findById(user.getId());
			
			if (org == null || userToRemove == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if(!removeFromOrganization(userToRemove, org))
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
			org.persist();
			
			// repersist, otherwise the user gets deleted by org.owners.remove(userToRemove)
			userToRemove.persist();
			
			// assign projects of removed user to oneself for now
			for (entity.core.PyroProjectDB project: org.projects) {
				project.owner = subject;
				project.persist();
			}
									
			return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(org, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response create(@javax.ws.rs.core.Context SecurityContext securityContext,PyroOrganization newOrg) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if (mayCreateOrganization(subject)) {
			final entity.core.PyroOrganizationDB org = this.createOrganization(newOrg.getname(),newOrg.getdescription(),subject);
			
			return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(org, objectCache)).build();
		}
		
		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	public entity.core.PyroOrganizationDB createOrganization(String name, String description, entity.core.PyroUserDB subject) {
		final entity.core.PyroOrganizationDB org = new entity.core.PyroOrganizationDB();
		org.name = name;
		org.description = description;
		
		final entity.core.PyroStyleDB style = new entity.core.PyroStyleDB();
		style.navBgColor = DefaultColors.NAV_BG_COLOR;
		style.navTextColor = DefaultColors.NAV_TEXT_COLOR;
		style.bodyBgColor = DefaultColors.BODY_BG_COLOR;
		style.bodyTextColor = DefaultColors.BODY_TEXT_COLOR;
		style.primaryBgColor = DefaultColors.PRIMARY_BG_COLOR;
		style.primaryTextColor = DefaultColors.PRIMARY_TEXT_COLOR;
		style.persist();
		
		org.style = style;
		org.persist();
		
		
		if(subject != null) {
			org.owners.add(subject);
			final entity.core.PyroOrganizationAccessRightVectorDB arv = createDefaultAccessRightVector(subject, org);
			arv.accessRights.add(entity.core.PyroOrganizationAccessRightDB.CREATE_PROJECTS);
			arv.accessRights.add(entity.core.PyroOrganizationAccessRightDB.EDIT_PROJECTS);
			arv.accessRights.add(entity.core.PyroOrganizationAccessRightDB.DELETE_PROJECTS);
			arv.persist();
			org.persist();
			
		}
		return org;
	}
	
	@javax.ws.rs.PUT
	@javax.ws.rs.Path("/{orgId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response update(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId, PyroOrganization organization) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroOrganizationDB orgInDB = entity.core.PyroOrganizationDB.findById(orgId);
			if (orgInDB == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if ((orgInDB.id != organization.getId()) || organization.getname().trim().equals("")) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
			}
			
			if (isOwnerOf(subject, orgInDB)) {
				orgInDB.name = organization.getname();
				orgInDB.description = organization.getdescription();
				orgInDB.persist();

				
				final entity.core.PyroStyleDB style = orgInDB.style;			
				style.navBgColor = organization.getstyle().getnavBgColor();
				style.navTextColor = organization.getstyle().getnavTextColor();
				style.bodyBgColor = organization.getstyle().getbodyBgColor();
				style.bodyTextColor = organization.getstyle().getbodyTextColor();
				style.primaryBgColor = organization.getstyle().getprimaryBgColor();
				style.primaryTextColor = organization.getstyle().getprimaryTextColor();
				
				if (organization.getstyle().getlogo() != null) {
					final entity.core.BaseFileDB logo = entity.core.BaseFileDB.findById(organization.getstyle().getlogo().getId());
					style.logo = logo;
				} else {
					style.logo = null;
				}
				style.persist();
				
				return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(orgInDB, objectCache)).build();
			}
		}
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.DELETE
	@javax.ws.rs.Path("/{orgId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response delete(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("orgId") final long orgId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroOrganizationDB orgInDB = entity.core.PyroOrganizationDB.findById(orgId);
			if (orgInDB == null) {
				return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			}
			
			if (isOrgManager(subject) || isOwnerOf(subject, orgInDB)) {
				deleteAllProjects(orgInDB, subject, securityContext);
				deleteAccessRightVectors(orgInDB);
				
				orgInDB.members.clear();
				orgInDB.owners.clear();
				orgInDB.projects.clear();
				orgInDB.delete();
				return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(orgInDB, objectCache)).build();
			}
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	private void deleteAllProjects(entity.core.PyroOrganizationDB org, entity.core.PyroUserDB subject, SecurityContext securityContext) {
		java.util.Iterator<entity.core.PyroProjectDB> iter = org.projects.iterator();
		while(iter.hasNext()) {
			entity.core.PyroProjectDB project = iter.next();
			projectService.deleteById(subject, project.id, securityContext);
			iter = org.projects.iterator();
		}
	}
	
	private entity.core.PyroOrganizationAccessRightVectorDB findAccessRightVector(
			entity.core.PyroUserDB user,
			entity.core.PyroOrganizationDB org) {
		
		
		final java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> result = entity.core.PyroOrganizationAccessRightVectorDB.list("user = ?1 and organization = ?2",user,org);
		return result.size() == 1 ? result.get(0) : null;
	}
	
	private java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> findAccessRightVectors(entity.core.PyroOrganizationDB org) {
		final java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> result = entity.core.PyroOrganizationAccessRightVectorDB.list("organization = ?1",org);
		return result;
	}
	
	private boolean mayCreateOrganization(entity.core.PyroUserDB user) {
		final entity.core.PyroSettingsDB settings = entity.core.PyroSettingsDB.listAll().stream().map(n->(entity.core.PyroSettingsDB)n).findFirst().orElse(null);
		
		return user != null && (isOrgManager(user) || settings.globallyCreateOrganizations);
	}
	
	private boolean accessRightVectorExists(
		entity.core.PyroUserDB user,
		entity.core.PyroOrganizationDB org) {
		return findAccessRightVector(user, org) != null;
	}
	
	private entity.core.PyroOrganizationAccessRightVectorDB createDefaultAccessRightVector(
			entity.core.PyroUserDB user,
			entity.core.PyroOrganizationDB org) {
		
		final entity.core.PyroOrganizationAccessRightVectorDB arv = new entity.core.PyroOrganizationAccessRightVectorDB();
		arv.user = user;
		arv.organization = org;
		arv.persist();
		
		return arv;
	}
	
	private void deleteOrganizationDependencies(entity.core.PyroUserDB user, entity.core.PyroOrganizationDB org) {
		deleteAccessRightVector(user, org);
	}
	
	private void deleteAccessRightVector(
				entity.core.PyroUserDB user,
				entity.core.PyroOrganizationDB org) {
			final entity.core.PyroOrganizationAccessRightVectorDB arv = findAccessRightVector(user, org);
			arv.delete();
		}
	
	private void deleteAccessRightVectors(entity.core.PyroOrganizationDB org) {
		java.util.List<entity.core.PyroOrganizationAccessRightVectorDB> accessRightVectors = findAccessRightVectors(org);
		for(entity.core.PyroOrganizationAccessRightVectorDB e : accessRightVectors) {
			e.accessRights.clear();
			e.organization = null;
			e.user = null;
			e.persist();
			e.delete();
		}
	}

	private boolean isOrgManager(entity.core.PyroUserDB user) {
		return user.systemRoles.contains(entity.core.PyroSystemRoleDB.ORGANIZATION_MANAGER);
	}
	
	private boolean isMemberOf(entity.core.PyroUserDB user, entity.core.PyroOrganizationDB org) {
		return org.members.contains(user);
	}
	
	private boolean isOwnerOf(entity.core.PyroUserDB user, entity.core.PyroOrganizationDB org) {
		return org.owners.contains(user);
	}
	
	public boolean removeFromOrganization(entity.core.PyroUserDB user, entity.core.PyroOrganizationDB org) {
		if (isOwnerOf(user, org) && org.owners.size() > 1) { // don't delete single owner
			deleteOrganizationDependencies(user, org);
			org.owners.remove(user);
		} else if (isMemberOf(user, org)) {
			deleteOrganizationDependencies(user, org);
			org.members.remove(user);
		} else {
			return false;
		}
		return true;
	}
}
