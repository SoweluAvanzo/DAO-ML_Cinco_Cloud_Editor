package info.scce.cincocloud.core;

import javax.servlet.ServletException;
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.auth.TokenUtils;
import info.scce.cincocloud.core.rest.types.AuthResponse;
import info.scce.cincocloud.core.rest.types.Login;
import info.scce.cincocloud.core.rest.types.PyroUser;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;

@javax.transaction.Transactional
@javax.ws.rs.Path("/user/current")
@javax.enterprise.context.RequestScoped
public class CurrentUserController {

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "info.scce.pyro.jwt.duration")
    public Long duration;

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "mp.jwt.verify.issuer")
    public String issuer;

    @javax.inject.Inject
    info.scce.cincocloud.rest.ObjectCache objectCache;

    @javax.inject.Inject
    PBKDF2Encoder passwordEncoder;

    @javax.ws.rs.POST
    @javax.ws.rs.Path("login")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @javax.annotation.security.PermitAll
    public javax.ws.rs.core.Response getCurrentUser(Login login) {
        final PyroUserDB subject = PyroUserDB.find("email", login.email).firstResult();
        if (subject != null && subject.password.equals(passwordEncoder.encode(login.password))) {
            AuthResponse auth = getAuthResponse(subject);
            if (auth != null) {
                return javax.ws.rs.core.Response.ok(auth).build();
            }
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED).build();
    }

    @javax.ws.rs.GET
    @javax.ws.rs.Path("logout")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response logout(
            @javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request,
            @javax.ws.rs.core.Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        try {
            // remove associated tickets
            TicketRegistrationHandler.removeTicketsOf(subject);
            // remove association between e.g. UserPrincipal and the SessionContext
            request.logout();
            return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.OK).build();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
    }

    @javax.ws.rs.GET
    @javax.ws.rs.Path("private")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response getCurrentUser(@javax.ws.rs.core.Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null) {
            PyroUser result = objectCache.getRestTo(subject);
            if (result == null) {
                result = PyroUser.fromEntity(subject, objectCache);
            }
            return javax.ws.rs.core.Response.ok(result).build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.PUT
    @javax.ws.rs.Path("update/private")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response update(@javax.ws.rs.core.Context SecurityContext securityContext, final PyroUser user) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        if (subject != null) {
            if (user.getemail() == null || user.getemail().trim().equals("")) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)
                        .entity("email may not be empty")
                        .build();
            }
            // update email and hash
            subject.email = user.getemail();
            if (user.getprofilePicture() != null) {
                final BaseFileDB picture = BaseFileDB.findById(user.getprofilePicture().getId());
                subject.profilePicture = picture;
            } else {
                if (subject.profilePicture != null) {
                    subject.profilePicture.delete();
                }
                subject.profilePicture = null;
            }
            subject.persist();
            // get new autentication since credentials could be changed
            AuthResponse auth = getAuthResponse(subject);
            return javax.ws.rs.core.Response.ok(auth).build();
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    public AuthResponse getAuthResponse(PyroUserDB subject) {
        try {
            return new AuthResponse(TokenUtils.generateToken(subject.email, "user", duration, issuer));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
