package info.scce.cincocloud.core;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.types.PyroUserRegistration;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;
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
      @Valid PyroUserRegistration pyroUserRegistration
  ) {
    final var emailExists = !PyroUserDB.list("email", pyroUserRegistration.getEmail()).isEmpty();

    if (!emailExists) {
      final var user = PyroUserDB.add(
          pyroUserRegistration.getEmail(),
          pyroUserRegistration.getUsername(),
          passwordEncoder.encode(pyroUserRegistration.getPassword())
      );

      if (PyroUserDB.count() == 1) {
        user.systemRoles.add(PyroSystemRoleDB.ADMIN);
        user.systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
      }

      user.persist();

      // TODO: SAMI: send activation mail
      // TODO: SAMI: remove this later (for development use)
      user.isActivated = true;

      return Response.ok("Activation mail send").build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }
}

