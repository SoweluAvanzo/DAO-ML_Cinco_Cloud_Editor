package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.tos.AuthResponseTO;
import info.scce.cincocloud.core.services.AuthService;
import info.scce.cincocloud.core.services.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/auth")
@RequestScoped
public class AuthController {

  @Inject
  AuthService authService;

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
