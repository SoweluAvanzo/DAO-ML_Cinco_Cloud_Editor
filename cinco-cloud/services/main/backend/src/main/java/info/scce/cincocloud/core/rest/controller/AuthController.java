package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.tos.AuthResponseTO;
import info.scce.cincocloud.core.services.AuthService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.rest.ObjectCache;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/auth")
@RequestScoped
public class AuthController {

  @Inject
  ObjectCache objectCache;

  @Inject
  AuthService authService;

  @Inject
  PBKDF2Encoder passwordEncoder;

  @POST
  @PermitAll
  public Response login(@Valid UserLoginInput login) {
    final var token = authService.login(login.emailOrUsername, login.password);
    return Response.ok(new AuthResponseTO(token)).build();
  }

  @DELETE
  @RolesAllowed("user")
  public Response logout(@Context SecurityContext securityContext) {
    final var subject = UserService.getCurrentUser(securityContext);

    authService.logout(subject);

    return Response.status(Response.Status.OK).build();
  }
}
