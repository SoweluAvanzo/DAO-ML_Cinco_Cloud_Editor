package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.core.services.RegistrationService;
import info.scce.cincocloud.core.services.SettingsService;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import jakarta.annotation.security.PermitAll;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

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
