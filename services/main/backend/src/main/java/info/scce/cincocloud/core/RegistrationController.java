package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
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
import javax.ws.rs.core.SecurityContext;

@Transactional
@Path("/register/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistrationController {

  @Inject
  RegistrationService registrationService;

  @POST
  @Path("new/public")
  @PermitAll
  public Response registerUser(
      @Context SecurityContext securityContext,
      @Valid UserRegistrationInput userRegistration
  ) {
    registrationService.registerUser(userRegistration);
    return Response.ok("Activation mail send").build();
  }
}
