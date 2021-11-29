package info.scce.cincocloud.core;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
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
  PBKDF2Encoder passwordEncoder;

  @POST
  @Path("new/public")
  @PermitAll
  public Response registerUser(
      @Context SecurityContext securityContext,
      @Valid UserRegistrationInput userRegistration
  ) {
    final var emailExists = !UserDB.list("email", userRegistration.getEmail()).isEmpty();
    final var passwordsAreNotEqual = !userRegistration.getPassword().equals(userRegistration.getPasswordConfirm());

    if (emailExists || passwordsAreNotEqual) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    final var user = UserDB.add(
        userRegistration.getEmail(),
        userRegistration.getUsername(),
        passwordEncoder.encode(userRegistration.getPassword())
    );

    if (UserDB.count() == 1) {
      user.systemRoles.add(UserSystemRole.ADMIN);
      user.systemRoles.add(UserSystemRole.ORGANIZATION_MANAGER);
    }

    user.persist();

    // TODO: SAMI: send activation mail
    // TODO: SAMI: remove this later (for development use)
    user.isActivated = true;

    return Response.ok("Activation mail send").build();
  }
}

