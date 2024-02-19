package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.tos.SettingsTO;
import info.scce.cincocloud.core.services.SettingsService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.rest.ObjectCache;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
    final var updatedSettings = settingsService.updateSettings(settings);
    return Response.ok(SettingsTO.fromEntity(updatedSettings, objectCache)).build();
  }
}
