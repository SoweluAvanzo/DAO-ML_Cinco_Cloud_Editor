package info.scce.cincocloud.core;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.inputs.UpdateCurrentUserInput;
import info.scce.cincocloud.core.rest.inputs.UpdateCurrentUserPasswordInput;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Path("/user/current")
@RequestScoped
public class CurrentUserController {

  @Inject
  ObjectCache objectCache;

  @Inject
  AuthService authService;

  @Inject
  PBKDF2Encoder passwordEncoder;

  @POST
  @Path("login")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @PermitAll
  public Response login(@Valid UserLoginInput login) {
    final var auth = authService.login(login);
    return Response.ok(auth).build();
  }

  @GET
  @Path("logout")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("user")
  public Response logout(
      @Context HttpServletRequest request,
      @Context SecurityContext securityContext
  ) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    try {
      // remove associated tickets
      TicketRegistrationHandler.removeTicketsOf(subject);
      // remove association between e.g. UserPrincipal and the SessionContext
      request.logout();
      return Response.status(Response.Status.OK).build();
    } catch (ServletException servletException) {
      throw new RestException(Response.Status.BAD_REQUEST, servletException.getMessage());
    }
  }

  @GET
  @Path("private")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("user")
  public Response getCurrentUser(@Context SecurityContext securityContext) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      UserTO result = objectCache.getRestTo(subject);
      if (result == null) {
        result = UserTO.fromEntity(subject, objectCache);
      }
      return Response.ok(result).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @PUT
  @Path("private")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext, @Valid final UpdateCurrentUserInput input) {
    final var subject = UserDB.getCurrentUser(securityContext);
    if (subject == null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    final var newEmailIsTaken = UserDB.find("email", input.email).count() > 0;
    if (!input.email.equals(subject.email) && newEmailIsTaken) {
      throw new RestException(Response.Status.BAD_REQUEST, "Email is already taken");
    }

    // update email and hash
    subject.email = input.email;
    subject.name = input.name;

    if (input.profilePicture != null) {
      subject.profilePicture = BaseFileDB.findById(input.profilePicture.getId());
    } else {
      if (subject.profilePicture != null) {
        subject.profilePicture.delete();
      }
      subject.profilePicture = null;
    }

    subject.persist();

    return Response.ok(UserTO.fromEntity(subject, objectCache)).build();
  }

  @PUT
  @Path("password/private")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("user")
  public Response updatePassword(
      @Context final SecurityContext securityContext,
      @Valid final UpdateCurrentUserPasswordInput input
  ) {
    final var subject = UserDB.getCurrentUser(securityContext);
    if (subject == null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    if (!subject.password.equals(passwordEncoder.encode(input.oldPassword))) {
      throw new RestException(Response.Status.BAD_REQUEST, "Current password incorrect!");
    }

    subject.password = passwordEncoder.encode(input.newPassword);
    subject.persist();

    // get new authentication since credentials could be changed
    return Response.ok(authService.generateToken(subject)).build();
  }
}
