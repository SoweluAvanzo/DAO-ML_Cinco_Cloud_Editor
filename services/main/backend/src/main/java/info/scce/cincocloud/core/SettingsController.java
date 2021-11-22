package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.SettingsTO;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.db.StyleDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
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

  @GET
  @Path("/public")
  @PermitAll()
  public Response get() {
    final List<SettingsDB> result = SettingsDB.findAll().list();
    return Response.ok(SettingsTO.fromEntity(result.get(0), objectCache)).build();
  }

  @PUT
  @Path("/")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext, final SettingsTO settings) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final SettingsDB settingsInDb = SettingsDB.findById(settings.getId());

    if (subject != null && isAdmin(subject) && settingsInDb != null) {
      final StyleDB style = settingsInDb.style;
      style.navBgColor = settings.getstyle().getnavBgColor();
      style.navTextColor = settings.getstyle().getnavTextColor();
      style.bodyBgColor = settings.getstyle().getbodyBgColor();
      style.bodyTextColor = settings.getstyle().getbodyTextColor();
      style.primaryBgColor = settings.getstyle().getprimaryBgColor();
      style.primaryTextColor = settings.getstyle().getprimaryTextColor();
      if (settings.getstyle().getlogo() != null) {
        final BaseFileDB logo = BaseFileDB.findById(settings.getstyle().getlogo().getId());
        style.logo = logo;
      } else {
        style.logo = null;
      }

      settingsInDb.globallyCreateOrganizations = settings.getgloballyCreateOrganizations();
      return Response.ok(SettingsTO.fromEntity(settingsInDb, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isAdmin(UserDB user) {
    return user.systemRoles.contains(UserSystemRole.ADMIN);
  }
}
