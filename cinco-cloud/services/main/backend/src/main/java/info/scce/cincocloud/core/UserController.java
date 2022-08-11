package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.inputs.UserSearchInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RequestScoped
public class UserController {

  @Inject
  UserService userService;

  @Inject
  ObjectCache objectCache;

  @Inject
  RegistrationService registrationService;

  /**
   * Get all users.
   */
  @GET
  @Path("/")
  @RolesAllowed("user")
  public Response getUsers(@Context SecurityContext securityContext) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && subject.isAdmin()) {

      final List<UserDB> result = UserDB.listAll();

      final List<UserTO> users = new java.util.ArrayList<>();
      for (UserDB user : result) {
        users.add(UserTO.fromEntity(user, objectCache));
      }

      return Response.ok(users).build();
    }
    throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to request user list");
  }

  /**
   * Create a new user as administrator.
   *
   * @param securityContext The security context.
   * @param user            The data to create a new user from.
   * @return The created user.
   */
  @POST
  @Path("/private")
  @RolesAllowed("admin")
  public Response createUser(@Context SecurityContext securityContext, @Valid UserRegistrationInput user) {
    final var createdUser = registrationService.registerUserInternal(user);
    return Response.status(Status.CREATED)
        .entity(UserTO.fromEntity(createdUser, objectCache))
        .build();
  }

  /**
   * Get a user by its username or email.
   */
  @POST
  @Path("/search")
  @RolesAllowed("user")
  public Response searchUser(@Context SecurityContext securityContext, UserSearchInput search) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final List<UserDB> resultByUsername = UserDB.list("username", search.getusernameOrEmail());
      if (resultByUsername.size() == 1) {
        return Response.ok(UserTO.fromEntity(resultByUsername.get(0), objectCache)).build();
      }

      final List<UserDB> resultByEmail = UserDB.list("email", search.getusernameOrEmail());
      if (resultByEmail.size() == 1) {
        return Response.ok(UserTO.fromEntity(resultByEmail.get(0), objectCache)).build();
      }

      return Response.status(Response.Status.NOT_FOUND).build();
    }

    throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to search for a user");
  }

  @DELETE
  @Path("/{userId}")
  @RolesAllowed("user")
  public Response delete(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    if (subject != null && (subject.isAdmin() || subject.id.equals(userId))) {
      final UserDB userToDelete = UserDB.findById(userId);
      // check if the user has open responsibilities
      // e.g. is the only admin, owns projects with members, owns organizations
      if(userCanBeDeleted(userToDelete)){
        userService.deleteUser(userToDelete);
        return Response.ok().build();
      } else {
        throw new RestException(Response.Status.BAD_REQUEST,
            "Could not delete user. User still has unresolved responsibilities.");
      }
    }

    throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to delete a user");
  }

  @POST
  @Path("/{userId}/roles/addAdmin")
  @RolesAllowed("user")
  public Response makeAdmin(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && subject.isAdmin()) {
      final UserDB user = UserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (!user.isAdmin()) {
        user.systemRoles.add(UserSystemRole.ADMIN);
      }

      return Response.ok(UserTO.fromEntity(user, objectCache)).build();
    }

    throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to promote a user");
  }

  @POST
  @Path("/{userId}/roles/removeAdmin")
  @RolesAllowed("user")
  public Response makeUser(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null && subject.isAdmin()) {
      final UserDB user = UserDB.findById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      // an admin should not remove his own admin rights
      if (user.isAdmin() && user.id.equals(subject.id)) {
        throw new RestException(Response.Status.BAD_REQUEST, "Cannot remove admin rights from oneself.");
      }

      user.systemRoles.remove(UserSystemRole.ADMIN);
      return Response.ok(UserTO.fromEntity(user, objectCache)).build();
    }

    throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to demote a user");
  }

  private boolean userCanBeDeleted(UserDB userToDelete) {
    final List<UserDB> result = UserDB.listAll();
    // a user cannot delete their account, if they're the only admin
    if ((userToDelete.isAdmin() && result.stream().filter(UserDB::isAdmin).count() > 1) || !userToDelete.isAdmin()) {
      // a user cannot delete their account, if they own projects with at least one other member
      if (userToDelete.personalProjects.stream().allMatch(project -> project.members.isEmpty())) {
        // a user cannot delete their account, if they are the sole owner of an organization
        if (userToDelete.ownedOrganizations.stream().noneMatch(org -> org.owners.size() == 1)) {
          return true;
        }
      }
    }
    return false;
  }
}
