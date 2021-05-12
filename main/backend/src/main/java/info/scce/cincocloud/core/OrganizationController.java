package info.scce.cincocloud.core;

import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.PyroOrganization;
import info.scce.cincocloud.core.rest.types.PyroUser;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.PyroOrganizationAccessRightDB;
import info.scce.cincocloud.db.PyroOrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroSettingsDB;
import info.scce.cincocloud.db.PyroStyleDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.util.DefaultColors;

@javax.transaction.Transactional
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Path("/organization")
@javax.enterprise.context.RequestScoped
public class OrganizationController {

    @javax.inject.Inject
    ProjectService projectService;

    @javax.inject.Inject
    ObjectCache objectCache;

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response getAll(@javax.ws.rs.core.Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null) {
            final java.util.List<PyroOrganizationDB> result = PyroOrganizationDB.listAll();

            final java.util.List<PyroOrganization> orgs = new java.util.LinkedList<>();
            if (isOrgManager(subject)) {
                for (PyroOrganizationDB org : result) {
                    orgs.add(PyroOrganization.fromEntity(org, objectCache));
                }
            } else {
                for (PyroOrganizationDB org : result) {
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
    public javax.ws.rs.core.Response get(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null) {
            final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);
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
    public javax.ws.rs.core.Response leave(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);
        if (subject != null) {
            if (!removeFromOrganization(subject, org)) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
            }
            org.persist();
            subject.persist();

            return javax.ws.rs.core.Response.ok().build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{orgId}/addMember")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response addMember(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId, PyroUser user) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);

        if (subject != null && (isOrgManager(subject) || isOwnerOf(subject, org))) {
            final PyroUserDB member = PyroUserDB.findById(user.getId());

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
    public javax.ws.rs.core.Response addOwner(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId, PyroUser user) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);

        if (subject != null && (isOrgManager(subject) || isOwnerOf(subject, org))) {
            final PyroUserDB owner = PyroUserDB.findById(user.getId());

            if (org == null || owner == null) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
            }

            org.members.remove(owner);

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
    public javax.ws.rs.core.Response removeUser(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId, PyroUser user) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);

        if (subject != null && (isOrgManager(subject) || isOwnerOf(subject, org))) {
            final PyroUserDB userToRemove = PyroUserDB.findById(user.getId());

            if (org == null || userToRemove == null) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
            }

            if (!removeFromOrganization(userToRemove, org)) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
            }
            org.persist();

            // repersist, otherwise the user gets deleted by org.owners.remove(userToRemove)
            userToRemove.persist();

            // assign projects of removed user to oneself for now
            for (PyroProjectDB project : org.projects) {
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
    public javax.ws.rs.core.Response create(@javax.ws.rs.core.Context SecurityContext securityContext, PyroOrganization newOrg) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (mayCreateOrganization(subject)) {
            final PyroOrganizationDB org = this.createOrganization(newOrg.getname(), newOrg.getdescription(), subject);

            return javax.ws.rs.core.Response.ok(PyroOrganization.fromEntity(org, objectCache)).build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    public PyroOrganizationDB createOrganization(String name, String description, PyroUserDB subject) {
        final PyroOrganizationDB org = new PyroOrganizationDB();
        org.name = name;
        org.description = description;

        final PyroStyleDB style = new PyroStyleDB();
        style.navBgColor = DefaultColors.NAV_BG_COLOR;
        style.navTextColor = DefaultColors.NAV_TEXT_COLOR;
        style.bodyBgColor = DefaultColors.BODY_BG_COLOR;
        style.bodyTextColor = DefaultColors.BODY_TEXT_COLOR;
        style.primaryBgColor = DefaultColors.PRIMARY_BG_COLOR;
        style.primaryTextColor = DefaultColors.PRIMARY_TEXT_COLOR;
        style.persist();

        org.style = style;
        org.persist();


        if (subject != null) {
            org.owners.add(subject);
            final PyroOrganizationAccessRightVectorDB arv = createDefaultAccessRightVector(subject, org);
            arv.accessRights.add(PyroOrganizationAccessRightDB.CREATE_PROJECTS);
            arv.accessRights.add(PyroOrganizationAccessRightDB.EDIT_PROJECTS);
            arv.accessRights.add(PyroOrganizationAccessRightDB.DELETE_PROJECTS);
            arv.persist();
            org.persist();

        }
        return org;
    }

    @javax.ws.rs.PUT
    @javax.ws.rs.Path("/{orgId}")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response update(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId, PyroOrganization organization) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null) {
            final PyroOrganizationDB orgInDB = PyroOrganizationDB.findById(orgId);
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


                final PyroStyleDB style = orgInDB.style;
                style.navBgColor = organization.getstyle().getnavBgColor();
                style.navTextColor = organization.getstyle().getnavTextColor();
                style.bodyBgColor = organization.getstyle().getbodyBgColor();
                style.bodyTextColor = organization.getstyle().getbodyTextColor();
                style.primaryBgColor = organization.getstyle().getprimaryBgColor();
                style.primaryTextColor = organization.getstyle().getprimaryTextColor();

                if (organization.getstyle().getlogo() != null) {
                    final BaseFileDB logo = BaseFileDB.findById(organization.getstyle().getlogo().getId());
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
    public javax.ws.rs.core.Response delete(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("orgId") final long orgId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null) {
            final PyroOrganizationDB orgInDB = PyroOrganizationDB.findById(orgId);
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

    private void deleteAllProjects(PyroOrganizationDB org, PyroUserDB subject, SecurityContext securityContext) {
        java.util.Iterator<PyroProjectDB> iter = org.projects.iterator();
        while (iter.hasNext()) {
            PyroProjectDB project = iter.next();
            projectService.deleteById(subject, project.id, securityContext);
            iter = org.projects.iterator();
        }
    }

    private PyroOrganizationAccessRightVectorDB findAccessRightVector(
            PyroUserDB user,
            PyroOrganizationDB org) {


        final java.util.List<PyroOrganizationAccessRightVectorDB> result = PyroOrganizationAccessRightVectorDB.list("user = ?1 and organization = ?2", user, org);
        return result.size() == 1 ? result.get(0) : null;
    }

    private java.util.List<PyroOrganizationAccessRightVectorDB> findAccessRightVectors(PyroOrganizationDB org) {
        final java.util.List<PyroOrganizationAccessRightVectorDB> result = PyroOrganizationAccessRightVectorDB.list("organization = ?1", org);
        return result;
    }

    private boolean mayCreateOrganization(PyroUserDB user) {
        final PyroSettingsDB settings = PyroSettingsDB.listAll().stream().map(n -> (PyroSettingsDB) n).findFirst().orElse(null);

        return user != null && (isOrgManager(user) || settings.globallyCreateOrganizations);
    }

    private boolean accessRightVectorExists(
            PyroUserDB user,
            PyroOrganizationDB org) {
        return findAccessRightVector(user, org) != null;
    }

    private PyroOrganizationAccessRightVectorDB createDefaultAccessRightVector(
            PyroUserDB user,
            PyroOrganizationDB org) {

        final PyroOrganizationAccessRightVectorDB arv = new PyroOrganizationAccessRightVectorDB();
        arv.user = user;
        arv.organization = org;
        arv.persist();

        return arv;
    }

    private void deleteOrganizationDependencies(PyroUserDB user, PyroOrganizationDB org) {
        deleteAccessRightVector(user, org);
    }

    private void deleteAccessRightVector(
            PyroUserDB user,
            PyroOrganizationDB org
    ) {
        final PyroOrganizationAccessRightVectorDB arv = findAccessRightVector(user, org);
        arv.delete();
    }

    private void deleteAccessRightVectors(PyroOrganizationDB org) {
        java.util.List<PyroOrganizationAccessRightVectorDB> accessRightVectors = findAccessRightVectors(org);
        for (PyroOrganizationAccessRightVectorDB e : accessRightVectors) {
            e.accessRights.clear();
            e.organization = null;
            e.user = null;
            e.persist();
            e.delete();
        }
    }

    private boolean isOrgManager(PyroUserDB user) {
        return user.systemRoles.contains(PyroSystemRoleDB.ORGANIZATION_MANAGER);
    }

    private boolean isMemberOf(PyroUserDB user, PyroOrganizationDB org) {
        return org.members.contains(user);
    }

    private boolean isOwnerOf(PyroUserDB user, PyroOrganizationDB org) {
        return org.owners.contains(user);
    }

    public boolean removeFromOrganization(PyroUserDB user, PyroOrganizationDB org) {
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
