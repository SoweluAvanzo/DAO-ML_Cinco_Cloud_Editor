package info.scce.cincocloud.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.PyroSettings;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.PyroSettingsDB;
import info.scce.cincocloud.db.PyroStyleDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;

@javax.ws.rs.Path("/settings")
@javax.transaction.Transactional
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class SettingsController {


    @javax.inject.Inject
    info.scce.cincocloud.rest.ObjectCache objectCache;

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/public")
    @javax.annotation.security.PermitAll()
    public javax.ws.rs.core.Response get() {
        final java.util.List<PyroSettingsDB> result = PyroSettingsDB.findAll().list();
        return javax.ws.rs.core.Response.ok(PyroSettings.fromEntity(result.get(0), objectCache)).build();
    }

    @javax.ws.rs.PUT
    @javax.ws.rs.Path("/")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response update(@javax.ws.rs.core.Context SecurityContext securityContext, final PyroSettings settings) {
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
            return javax.ws.rs.core.Response.ok(PyroSettings.fromEntity(settingsInDb, objectCache)).build();
        }

        return javax.ws.rs.core.Response.status(Response.Status.FORBIDDEN).build();
    }

    private boolean isAdmin(PyroUserDB user) {
        return user.systemRoles.contains(PyroSystemRoleDB.ADMIN);
    }
}
