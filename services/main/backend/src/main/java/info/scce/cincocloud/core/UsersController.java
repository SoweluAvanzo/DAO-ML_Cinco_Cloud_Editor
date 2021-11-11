package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.types.PyroUser;
import info.scce.cincocloud.core.rest.types.PyroUserSearch;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RequestScoped
public class UsersController {

  @Inject
  OrganizationController organizationController;

  @Inject
  ObjectCache objectCache;

  /**
   * Get all users.
   */
  @GET
  @Path("/")
  @RolesAllowed("user")
  public Response getUsers(@Context SecurityContext securityContext) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {

      final List<PyroUserDB> result = PyroUserDB.listAll();

      final List<PyroUser> users = new java.util.ArrayList<>();
      for (PyroUserDB user : result) {
        users.add(PyroUser.fromEntity(user, objectCache));
      }

      return Response.ok(users).build();
    }

    return Response.status(
        Response.Status.FORBIDDEN).build();
  }

  /**
   * Get a user by its username or email.
   */
  @POST
  @Path("/search")
  @RolesAllowed("user")
  public Response searchUser(@Context SecurityContext securityContext, PyroUserSearch search) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {

      final List<PyroUserDB> resultByUsername = PyroUserDB
          .list("username", search.getusernameOrEmail());
      if (resultByUsername.size() == 1) {
        return Response.ok(PyroUser.fromEntity(resultByUsername.get(0), objectCache)).build();
      }

      final List<PyroUserDB> resultByEmail = PyroUserDB.list("email", search.getusernameOrEmail());
      if (resultByEmail.size() == 1) {
        return Response.ok(PyroUser.fromEntity(resultByEmail.get(0), objectCache)).build();
      }

      return Response.status(
          Response.Status.NOT_FOUND).build();
    }

    return Response.status(
        Response.Status.FORBIDDEN).build();
  }

  @DELETE
  @Path("/{userId}")
  @RolesAllowed("user")
  public Response delete(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
    if (subject != null && isAdmin(subject)) {
      final PyroUserDB userToDelete = PyroUserDB.findById(userId);
      if (subject.equals(userToDelete)) { // an admin should not delete himself
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      deleteUser(userToDelete);
      return Response.ok().build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/addAdmin")
  @RolesAllowed("user")
  public Response makeAdmin(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final PyroUserDB user = PyroUserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (!user.systemRoles.contains(PyroSystemRoleDB.ADMIN)) {
        user.systemRoles.add(PyroSystemRoleDB.ADMIN);
        user.systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
      }

      return Response.ok(PyroUser.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/removeAdmin")
  @RolesAllowed("user")
  public Response makeUser(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final PyroUserDB user = PyroUserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      // an admin should not remove his own admin rights
      if (isAdmin(user) && user.id.equals(subject.id)) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }

      user.systemRoles.remove(PyroSystemRoleDB.ADMIN);
      return Response.ok(PyroUser.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/addOrgManager")
  @RolesAllowed("user")
  public Response addOrgManager(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final PyroUserDB user = PyroUserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (!user.systemRoles.contains(PyroSystemRoleDB.ORGANIZATION_MANAGER)) {
        user.systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
      }

      return Response.ok(PyroUser.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/removeOrgManager")
  @RolesAllowed("user")
  public Response removeOrgManager(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final PyroUserDB user = PyroUserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      user.systemRoles.remove(PyroSystemRoleDB.ORGANIZATION_MANAGER);
      return Response.ok(PyroUser.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isAdmin(PyroUserDB user) {
    return user.systemRoles.contains(PyroSystemRoleDB.ADMIN);
  }

  public void deleteUser(PyroUserDB user) {
    List<PyroOrganizationDB> orgs = PyroOrganizationDB.listAll();
    orgs.forEach((org) -> {
      if (org.owners.contains(user) || org.members.contains(user)) {
        this.organizationController.removeFromOrganization(user, org);
      }
    });
    user.delete();
  }
}
