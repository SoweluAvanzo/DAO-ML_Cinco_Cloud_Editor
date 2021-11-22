package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.inputs.UserSearchInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
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
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {

      final List<UserDB> result = UserDB.listAll();

      final List<UserTO> users = new java.util.ArrayList<>();
      for (UserDB user : result) {
        users.add(UserTO.fromEntity(user, objectCache));
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
  public Response searchUser(@Context SecurityContext securityContext, UserSearchInput search) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {

      final List<UserDB> resultByUsername = UserDB
          .list("username", search.getusernameOrEmail());
      if (resultByUsername.size() == 1) {
        return Response.ok(UserTO.fromEntity(resultByUsername.get(0), objectCache)).build();
      }

      final List<UserDB> resultByEmail = UserDB.list("email", search.getusernameOrEmail());
      if (resultByEmail.size() == 1) {
        return Response.ok(UserTO.fromEntity(resultByEmail.get(0), objectCache)).build();
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
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    if (subject != null && isAdmin(subject)) {
      final UserDB userToDelete = UserDB.findById(userId);
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
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final UserDB user = UserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (!user.systemRoles.contains(UserSystemRole.ADMIN)) {
        user.systemRoles.add(UserSystemRole.ADMIN);
        user.systemRoles.add(UserSystemRole.ORGANIZATION_MANAGER);
      }

      return Response.ok(UserTO.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/removeAdmin")
  @RolesAllowed("user")
  public Response makeUser(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final UserDB user = UserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      // an admin should not remove his own admin rights
      if (isAdmin(user) && user.id.equals(subject.id)) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }

      user.systemRoles.remove(UserSystemRole.ADMIN);
      return Response.ok(UserTO.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/addOrgManager")
  @RolesAllowed("user")
  public Response addOrgManager(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final UserDB user = UserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (!user.systemRoles.contains(UserSystemRole.ORGANIZATION_MANAGER)) {
        user.systemRoles.add(UserSystemRole.ORGANIZATION_MANAGER);
      }

      return Response.ok(UserTO.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{userId}/roles/removeOrgManager")
  @RolesAllowed("user")
  public Response removeOrgManager(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && isAdmin(subject)) {
      final UserDB user = UserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      user.systemRoles.remove(UserSystemRole.ORGANIZATION_MANAGER);
      return Response.ok(UserTO.fromEntity(user, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isAdmin(UserDB user) {
    return user.systemRoles.contains(UserSystemRole.ADMIN);
  }

  public void deleteUser(UserDB user) {
    List<OrganizationDB> orgs = OrganizationDB.listAll();
    orgs.forEach((org) -> {
      if (org.owners.contains(user) || org.members.contains(user)) {
        this.organizationController.removeFromOrganization(user, org);
      }
    });
    user.delete();
  }
}
