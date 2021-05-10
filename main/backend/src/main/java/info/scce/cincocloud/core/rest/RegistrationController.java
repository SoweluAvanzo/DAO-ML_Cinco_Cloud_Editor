package info.scce.cincocloud.core.rest;

import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.types.PyroUserRegistration;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;

@javax.transaction.Transactional
@javax.ws.rs.Path("/register/")
public class RegistrationController {

    private static final int MIN_PASSWORD_LENGTH = 5;


    @javax.inject.Inject
    PBKDF2Encoder passwordEncoder;

    @javax.inject.Inject
    info.scce.cincocloud.rest.ObjectCache objectCache;

    @javax.inject.Inject
    info.scce.cincocloud.core.OrganizationController organizationController;

    @javax.ws.rs.POST
    @javax.ws.rs.Path("new/public")
    @javax.annotation.security.PermitAll
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response registerUser(@javax.ws.rs.core.Context SecurityContext securityContext, PyroUserRegistration pyroUserRegistration) {

        if (
                pyroUserRegistration.getemail() == null ||
                        pyroUserRegistration.getusername() == null ||
                        pyroUserRegistration.getname() == null ||
                        pyroUserRegistration.getpassword() == null
        ) {
            return javax.ws.rs.core.Response.status(
                    javax.ws.rs.core.Response.Status.FORBIDDEN
            ).build();
        }

        if (
                pyroUserRegistration.getemail().isEmpty() ||
                        pyroUserRegistration.getusername().isEmpty() ||
                        pyroUserRegistration.getname().isEmpty() ||
                        pyroUserRegistration.getpassword().isEmpty()
        ) {
            return javax.ws.rs.core.Response.status(
                    javax.ws.rs.core.Response.Status.FORBIDDEN
            ).build();
        }

        if (pyroUserRegistration.getpassword().length() < MIN_PASSWORD_LENGTH) {
            return javax.ws.rs.core.Response.status(
                    javax.ws.rs.core.Response.Status.FORBIDDEN
            ).build();
        }

        final java.util.List<PyroUserDB> users = PyroUserDB.list("email", pyroUserRegistration.getemail());

        boolean userShouldBeAdmin = PyroUserDB.count() <= 0;
        if (users.isEmpty()) {
            PyroUserDB user = PyroUserDB.add(
                    pyroUserRegistration.getemail(),
                    pyroUserRegistration.getusername(),
                    passwordEncoder.encode(pyroUserRegistration.getpassword())
            );
            if (userShouldBeAdmin) {
                user.systemRoles.add(PyroSystemRoleDB.ADMIN);
                user.systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
            }
            user.persist();

            // TODO: SAMI: send activation mail
            user.isActivated = true; // TODO: SAMI: remove this later (for development use)

            return javax.ws.rs.core.Response.ok("Activation mail send").build();
        }

        return javax.ws.rs.core.Response.status(
                javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }
}

