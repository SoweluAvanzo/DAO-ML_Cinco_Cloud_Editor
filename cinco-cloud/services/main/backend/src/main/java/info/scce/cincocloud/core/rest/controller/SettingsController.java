package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.tos.SettingsTO;
import info.scce.cincocloud.core.services.SettingsService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.rest.ObjectCache;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/settings")
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class SettingsController {

  @Inject
  ObjectCache objectCache;

  @Inject
  SettingsService settingsService;

  @GET
  @PermitAll()
  public Response get() {
    return Response.ok(SettingsTO.fromEntity(settingsService.getSettings(), objectCache)).build();
  }

  @PUT
  @RolesAllowed("admin")
  public Response update(@Context SecurityContext securityContext, final SettingsTO settings) {
    UserService.getCurrentUser(securityContext);

    settingsService.setAllowPublicUserRegistration(settings.getallowPublicUserRegistration());
    settingsService.setAutoActivateUsers(settings.getautoActivateUsers());
    final var settingsInDb = settingsService.setSendMails(settings.getsendMails());

    return Response.ok(SettingsTO.fromEntity(settingsInDb, objectCache)).build();
  }
}
