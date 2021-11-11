package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.types.PyroSettings;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.PyroSettingsDB;
import info.scce.cincocloud.db.PyroStyleDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;
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
    final List<PyroSettingsDB> result = PyroSettingsDB.findAll().list();
    return Response.ok(PyroSettings.fromEntity(result.get(0), objectCache)).build();
  }

  @PUT
  @Path("/")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext, final PyroSettings settings) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
    final PyroSettingsDB settingsInDb = PyroSettingsDB.findById(settings.getId());

    if (subject != null && isAdmin(subject) && settingsInDb != null) {
      final PyroStyleDB style = settingsInDb.style;
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
      return Response.ok(PyroSettings.fromEntity(settingsInDb, objectCache)).build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isAdmin(PyroUserDB user) {
    return user.systemRoles.contains(PyroSystemRoleDB.ADMIN);
  }
}
