package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.inputs.ActivateUserInput;
import info.scce.cincocloud.core.rest.inputs.UpdateCurrentUserInput;
import info.scce.cincocloud.core.rest.inputs.UpdateCurrentUserPasswordInput;
import info.scce.cincocloud.core.rest.inputs.UpdateUserRolesInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.AuthResponseTO;
import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.core.services.AuthService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
  PBKDF2Encoder passwordEncoder;

  @Inject
  ObjectCache objectCache;

  @Inject
  AuthService authService;

  /**
   * Get all users.
   */
  @GET
  @RolesAllowed("user")
  public Response getUsers(@Context SecurityContext securityContext,
                           @QueryParam("search") final Optional<String> search,
                           @QueryParam("page") @DefaultValue("0") final int index,
                           @QueryParam("size") @DefaultValue("25") final int size,
                           @QueryParam("role") final Optional<UserSystemRole> systemRole) {
    UserService.getCurrentUser(securityContext);

    final PanacheQuery<UserDB> page = search.isPresent()
      ? userService.searchUsers(index, size, search.get(), systemRole)
      : userService.getUsers(index, size, systemRole);

    final var users = page.stream()
            .map(user -> UserTO.fromEntity(user, objectCache))
            .collect(Collectors.toList());

    final var pageTO = new PageTO<>(users, index, size, page.pageCount(), page.hasPreviousPage(), page.hasNextPage());
    return Response.ok(pageTO).build();
  }

  /**
   * Create a new user as administrator.
   *
   * @param securityContext The security context.
   * @param user            The data to create a new user from.
   * @return The created user.
   */
  @POST
  @RolesAllowed("admin")
  public Response createUser(@Context SecurityContext securityContext, @Valid UserRegistrationInput user) {
    final var createdUser = userService.create(user.getEmail(), user.getUsername(), user.getName(), passwordEncoder.encode(user.getPassword()));
    userService.activateUser(createdUser, false);

    return Response.status(Status.CREATED)
        .entity(UserTO.fromEntity(createdUser, objectCache))
        .build();
  }

  @GET
  @Path("/current")
  @RolesAllowed("user")
  public Response getCurrentUser(@Context SecurityContext securityContext) {
    final UserDB subject = UserService.getCurrentUser(securityContext);

    UserTO result = objectCache.getRestTo(subject);
    if (result == null) {
      result = UserTO.fromEntity(subject, objectCache);
    }

    return Response.ok(result).build();
  }

  @PUT
  @Path("{userId}/rpc/activate")
  @PermitAll
  public Response activateUser(
      @Context SecurityContext securityContext,
      @PathParam("userId") final long userId,
      @Valid ActivateUserInput input
  ) {
      final var subject = UserService.getCurrentUserOptional(securityContext);
      final var userToActivate = userService.getOrThrow(userId);

      if (subject.isPresent() && subject.get().isAdmin()) {
        final var updatedUser = userService.activateUser(userToActivate, false);
        return Response.ok(UserTO.fromEntity(updatedUser, objectCache)).build();
      }

      if (userToActivate.isDeactivatedByAdmin) {
        throw new RestException(Status.BAD_REQUEST, "Your account has been deactivated permanently.");
      } else if (userToActivate.isActivated) {
        throw new RestException(Status.BAD_REQUEST, "Your account is already activated.");
      } else if (!userToActivate.activationKey.equals(input.activationToken)) {
        throw new RestException(Status.BAD_REQUEST, "The activation token is invalid.");
      }

      final var updatedUser = userService.activateUser(userToActivate, true);
      return Response.ok(UserTO.fromEntity(updatedUser, objectCache)).build();
  }

  @PUT
  @Path("/{userId}/rpc/deactivate")
  @RolesAllowed("admin")
  public Response deactivateUser(
      @Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final var userToDeactivate = userService.getOrThrow(userId);

    if (subject.id.equals(userToDeactivate.id)) {
      throw new RestException(Status.BAD_REQUEST, "You cannot deactivate your own account.");
    }

    final var updatedUser = userService.deactivateUser(userToDeactivate);

    return Response.ok(UserTO.fromEntity(updatedUser, objectCache)).build();
  }

  @PUT
  @Path("/{userId}")
  @RolesAllowed("user")
  public Response update(
      @Context SecurityContext securityContext,
      @PathParam("userId") final long userId,
      @Valid final UpdateCurrentUserInput input) {
    final var subject = UserService.getCurrentUser(securityContext);

    if (subject.id != userId) {
      throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to update user.");
    }

    userService.updateEmail(subject, input.email);
    userService.updateName(subject, input.name);
    userService.updateProfilePicture(subject, Optional.ofNullable(input.profilePicture != null ? input.profilePicture.getId() : null));

    return Response.ok(UserTO.fromEntity(subject, objectCache)).build();
  }

  @PUT
  @Path("/{userId}/password")
  @RolesAllowed("user")
  public Response updatePassword(
      @Context final SecurityContext securityContext,
      @PathParam("userId") final long userId,
      @Valid final UpdateCurrentUserPasswordInput input
  ) {
    final var subject = UserService.getCurrentUser(securityContext);

    if (subject.id != userId) {
      throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to update user.");
    }

    if (!subject.password.equals(passwordEncoder.encode(input.oldPassword))) {
      throw new IllegalArgumentException("Current password incorrect!");
    }

    userService.updatePassword(subject, input.newPassword);

    // get new authentication since credentials could be changed
    return Response.ok(new AuthResponseTO(authService.generateToken(subject))).build();
  }

  @DELETE
  @Path("/{userId}")
  @RolesAllowed("user")
  public Response delete(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserService.getCurrentUser(securityContext);

    if (!(subject.isAdmin() || subject.id.equals(userId))) {
      throw new RestException(Response.Status.FORBIDDEN, "Missing permissions to delete a user");
    }

    userService.delete(userId);

    return Response.ok().build();
  }

  @PUT
  @Path("/{userId}/roles")
  @RolesAllowed("admin")
  public Response makeAdmin(@Context SecurityContext securityContext,
      @PathParam("userId") final long userId,
      @Valid final UpdateUserRolesInput input) {
    final UserDB subject = UserService.getCurrentUser(securityContext);

    // an admin should not remove his own admin rights
    if (userId == subject.id) {
      throw new RestException(Response.Status.BAD_REQUEST, "Cannot remove admin rights from oneself.");
    }

    final var updatedUser = userService.setAdmin(userId, input.getAdmin());

    return Response.ok(UserTO.fromEntity(updatedUser, objectCache)).build();
  }
}
