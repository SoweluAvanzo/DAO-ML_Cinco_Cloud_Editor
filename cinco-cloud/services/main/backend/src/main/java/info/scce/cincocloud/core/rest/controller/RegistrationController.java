package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.core.services.RegistrationService;
import info.scce.cincocloud.core.services.SettingsService;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Path("/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistrationController {

  @Inject
  RegistrationService registrationService;

  @Inject
  SettingsService settingsService;

  @Inject
  ObjectCache objectCache;

  @POST
  @PermitAll
  public Response registerUser(
      @Context SecurityContext securityContext,
      @Valid UserRegistrationInput userRegistration
  ) {
    if (!settingsService.getSettings().allowPublicUserRegistration) {
      throw new RestException(Status.FORBIDDEN, "User registration is currently disabled.");
    }

    if (!userRegistration.getPassword().equals(userRegistration.getPasswordConfirm())) {
      throw new RestException(Status.BAD_REQUEST, "The passwords do not match.");
    }

    final var createdUser = registrationService.registerUser(
            userRegistration.getName(),
            userRegistration.getUsername(),
            userRegistration.getEmail(),
            userRegistration.getPassword());

    final var createdUserTO = UserTO.fromEntity(createdUser, objectCache);
    return Response.ok(createdUserTO).build();
  }
}
