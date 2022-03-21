package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.OrganizationTO;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.OrganizationAccessRight;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/organization")
@RequestScoped
public class OrganizationController {

  @Inject
  OrganizationService organizationService;

  @Inject
  ObjectCache objectCache;

  @GET
  @Path("/")
  @RolesAllowed("user")
  public Response getAll(@Context SecurityContext securityContext) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final List<OrganizationDB> result = OrganizationDB.listAll();

      final List<OrganizationTO> orgs = new LinkedList<>();
      for (OrganizationDB org : result) {
        if (org.members.contains(subject) || org.owners.contains(subject)) {
          orgs.add(OrganizationTO.fromEntity(org, objectCache));
        }
      }

      return Response.ok(orgs).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @GET
  @Path("/{orgId}")
  @RolesAllowed("user")
  public Response get(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB org = OrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (isMemberOf(subject, org) || isOwnerOf(subject, org)) {
        return Response.ok(OrganizationTO.fromEntity(org, objectCache)).build();
      }
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{orgId}/leave")
  @RolesAllowed("user")
  public Response leave(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    final OrganizationDB org = OrganizationDB.findById(orgId);
    if (subject != null) {
      if (!removeFromOrganization(subject, org)) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      org.persist();
      subject.persist();

      return Response.ok().build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{orgId}/addMember")
  @RolesAllowed("user")
  public Response addMember(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId, UserTO user) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    final OrganizationDB org = OrganizationDB.findById(orgId);

    if (subject != null && isOwnerOf(subject, org)) {
      final UserDB member = UserDB.findById(user.getId());

      if (org == null || member == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (org.owners.contains(member)) {
        if (org.owners.size() == 1) { // do not make the ownly owner a member of the org
          return Response.status(Response.Status.BAD_REQUEST).build();
        }
        org.owners.remove(member);
      }

      if (!accessRightVectorExists(member, org)) {
        createDefaultAccessRightVector(member, org);
      }

      org.members.add(member);
      org.persist();
      return Response.ok(OrganizationTO.fromEntity(org, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{orgId}/addOwner")
  @RolesAllowed("user")
  public Response addOwner(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId, UserTO user) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    final OrganizationDB org = OrganizationDB.findById(orgId);

    if (subject != null && isOwnerOf(subject, org)) {
      final UserDB owner = UserDB.findById(user.getId());

      if (org == null || owner == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      org.members.remove(owner);

      if (!accessRightVectorExists(owner, org)) {
        createDefaultAccessRightVector(owner, org);
      }

      org.owners.add(owner);
      org.persist();
      return Response.ok(OrganizationTO.fromEntity(org, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{orgId}/removeUser")
  @RolesAllowed("user")
  public Response removeUser(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId, UserTO user) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    final OrganizationDB org = OrganizationDB.findById(orgId);

    if (subject != null && isOwnerOf(subject, org)) {
      final UserDB userToRemove = UserDB.findById(user.getId());

      if (org == null || userToRemove == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (!removeFromOrganization(userToRemove, org)) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      org.persist();

      // repersist, otherwise the user gets deleted by org.owners.remove(userToRemove)
      userToRemove.persist();

      return Response.ok(OrganizationTO.fromEntity(org, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/")
  @RolesAllowed("user")
  public Response create(@Context SecurityContext securityContext, OrganizationTO newOrg) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB org = this.createOrganization(newOrg.getname(), newOrg.getdescription(), subject);
      return Response.ok(OrganizationTO.fromEntity(org, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  public OrganizationDB createOrganization(
      String name,
      String description,
      UserDB subject
  ) {
    final var nameExists =
        !OrganizationDB.list("name", name).isEmpty() ||
            !UserDB.list("username", name).isEmpty();
    if (nameExists) {
      throw new RestException("The name already exists");
    }

    final OrganizationDB org = new OrganizationDB();
    org.name = name;
    org.description = description;
    org.persist();

    if (subject != null) {
      org.owners.add(subject);
      final OrganizationAccessRightVectorDB arv = createDefaultAccessRightVector(subject, org);
      arv.accessRights.add(OrganizationAccessRight.CREATE_PROJECTS);
      arv.accessRights.add(OrganizationAccessRight.EDIT_PROJECTS);
      arv.accessRights.add(OrganizationAccessRight.DELETE_PROJECTS);
      arv.persist();
      org.persist();

    }
    return org;
  }

  @PUT
  @Path("/{orgId}")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId, OrganizationTO organization) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB orgInDB = OrganizationDB.findById(orgId);
      if (orgInDB == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if ((orgInDB.id != organization.getId()) || organization.getname().trim().equals("")) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if (isOwnerOf(subject, orgInDB)) {
        orgInDB.name = organization.getname();
        orgInDB.description = organization.getdescription();

        if (organization.getlogo() != null) {
          orgInDB.logo = BaseFileDB.findById(organization.getlogo().getId());
        } else {
          orgInDB.logo = null;
        }

        orgInDB.persist();

        return Response.ok(OrganizationTO.fromEntity(orgInDB, objectCache)).build();
      }
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @DELETE
  @Path("/{orgId}")
  @RolesAllowed("user")
  public Response delete(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB orgInDB = OrganizationDB.findById(orgId);
      if (orgInDB == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (isOwnerOf(subject, orgInDB)) {
        organizationService.deleteOrganization(orgInDB);
        return Response.ok(OrganizationTO.fromEntity(orgInDB, objectCache)).build();
      }
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private OrganizationAccessRightVectorDB findAccessRightVector(
      UserDB user,
      OrganizationDB org) {

    final List<OrganizationAccessRightVectorDB> result = OrganizationAccessRightVectorDB
        .list("user = ?1 and organization = ?2", user, org);
    return result.size() == 1 ? result.get(0) : null;
  }

  private boolean accessRightVectorExists(
      UserDB user,
      OrganizationDB org) {
    return findAccessRightVector(user, org) != null;
  }

  private OrganizationAccessRightVectorDB createDefaultAccessRightVector(
      UserDB user,
      OrganizationDB org) {

    final OrganizationAccessRightVectorDB arv = new OrganizationAccessRightVectorDB();
    arv.user = user;
    arv.organization = org;
    arv.persist();

    return arv;
  }

  private void deleteOrganizationDependencies(UserDB user, OrganizationDB org) {
    deleteAccessRightVector(user, org);
  }

  private void deleteAccessRightVector(
      UserDB user,
      OrganizationDB org
  ) {
    final OrganizationAccessRightVectorDB arv = findAccessRightVector(user, org);
    arv.delete();
  }

  private boolean isMemberOf(UserDB user, OrganizationDB org) {
    return org.members.contains(user);
  }

  private boolean isOwnerOf(UserDB user, OrganizationDB org) {
    return org.owners.contains(user);
  }

  public boolean removeFromOrganization(UserDB user, OrganizationDB org) {
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
