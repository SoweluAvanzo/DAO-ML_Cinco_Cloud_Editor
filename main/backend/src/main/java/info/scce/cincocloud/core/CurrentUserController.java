package info.scce.cincocloud.core;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.auth.TokenUtils;
import info.scce.cincocloud.core.rest.types.AuthResponse;
import info.scce.cincocloud.core.rest.types.Login;
import info.scce.cincocloud.core.rest.types.PyroUser;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;

@Transactional
@Path("/user/current")
@RequestScoped
public class CurrentUserController {

    @ConfigProperty(name = "info.scce.pyro.jwt.duration")
    Long duration;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Inject
    ObjectCache objectCache;

    @Inject
    PBKDF2Encoder passwordEncoder;

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCurrentUser(Login login) {
        final PyroUserDB subject = PyroUserDB.find("email", login.email).firstResult();
        if (subject != null && subject.password.equals(passwordEncoder.encode(login.password))) {
            AuthResponse auth = getAuthResponse(subject);
            if (auth != null) {
                return Response.ok(auth).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response logout(
            @Context HttpServletRequest request,
            @Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        try {
            // remove associated tickets
            TicketRegistrationHandler.removeTicketsOf(subject);
            // remove association between e.g. UserPrincipal and the SessionContext
            request.logout();
            return Response.status(Response.Status.OK).build();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("private")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null) {
            PyroUser result = objectCache.getRestTo(subject);
            if (result == null) {
                result = PyroUser.fromEntity(subject, objectCache);
            }
            return Response.ok(result).build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @PUT
    @Path("update/private")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response update(@Context SecurityContext securityContext, final PyroUser user) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        if (subject != null) {
            if (user.getemail() == null || user.getemail().trim().equals("")) {
                return Response.status(Response.Status.BAD_REQUEST)
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
            // get new authentication since credentials could be changed
            AuthResponse auth = getAuthResponse(subject);
            return Response.ok(auth).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
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
