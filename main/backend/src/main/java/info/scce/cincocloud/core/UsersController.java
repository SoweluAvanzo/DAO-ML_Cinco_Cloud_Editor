package info.scce.cincocloud.core;

import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.PyroUser;
import info.scce.cincocloud.core.rest.types.PyroUserSearch;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;

@javax.ws.rs.Path("/users")
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.transaction.Transactional
@javax.enterprise.context.RequestScoped
public class UsersController {

    @javax.inject.Inject
    OrganizationController organizationController;

    @javax.inject.Inject
    info.scce.cincocloud.rest.ObjectCache objectCache;

    /** Get all users. */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response getUsers(@javax.ws.rs.core.Context SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null && isAdmin(subject)) {

            final java.util.List<PyroUserDB> result = PyroUserDB.listAll();

            final java.util.List<PyroUser> users = new java.util.ArrayList<>();
            for (PyroUserDB user : result) {
                users.add(PyroUser.fromEntity(user, objectCache));
            }

            return javax.ws.rs.core.Response.ok(users).build();
        }

        return javax.ws.rs.core.Response.status(
                javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    /** Get a user by its username or email. */
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/search")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response searchUser(@javax.ws.rs.core.Context SecurityContext securityContext, PyroUserSearch search) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null && isAdmin(subject)) {

            final java.util.List<PyroUserDB> resultByUsername = PyroUserDB.list("username", search.getusernameOrEmail());
            if (resultByUsername.size() == 1) {
                return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(resultByUsername.get(0), objectCache)).build();
            }

            final java.util.List<PyroUserDB> resultByEmail = PyroUserDB.list("email", search.getusernameOrEmail());
            if (resultByEmail.size() == 1) {
                return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(resultByEmail.get(0), objectCache)).build();
            }

            return javax.ws.rs.core.Response.status(
                    javax.ws.rs.core.Response.Status.NOT_FOUND).build();
        }

        return javax.ws.rs.core.Response.status(
                javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.DELETE
    @javax.ws.rs.Path("/{userId}")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response delete(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        if (subject != null && isAdmin(subject)) {
            final PyroUserDB userToDelete = PyroUserDB.findById(userId);
            if (subject.equals(userToDelete)) { // an admin should not delete himself
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
            }
            deleteUser(userToDelete);
            return javax.ws.rs.core.Response.ok().build();
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{userId}/roles/addAdmin")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response makeAdmin(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null && isAdmin(subject)) {
            final PyroUserDB user = PyroUserDB.findById(userId);
            if (user == null) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
            }

            if (!user.systemRoles.contains(PyroSystemRoleDB.ADMIN)) {
                user.systemRoles.add(PyroSystemRoleDB.ADMIN);
                user.systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
            }

            return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user, objectCache)).build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{userId}/roles/removeAdmin")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response makeUser(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null && isAdmin(subject)) {
            final PyroUserDB user = PyroUserDB.findById(userId);
            if (user == null) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
            }

            // an admin should not remove his own admin rights
            if (isAdmin(user) && user.id.equals(subject.id)) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).build();
            }

            user.systemRoles.remove(PyroSystemRoleDB.ADMIN);
            return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user, objectCache)).build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{userId}/roles/addOrgManager")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response addOrgManager(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null && isAdmin(subject)) {
            final PyroUserDB user = PyroUserDB.findById(userId);
            if (user == null) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
            }

            if (!user.systemRoles.contains(PyroSystemRoleDB.ORGANIZATION_MANAGER)) {
                user.systemRoles.add(PyroSystemRoleDB.ORGANIZATION_MANAGER);
            }

            return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user, objectCache)).build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{userId}/roles/removeOrgManager")
    @javax.annotation.security.RolesAllowed("user")
    public javax.ws.rs.core.Response removeOrgManager(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("userId") final long userId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (subject != null && isAdmin(subject)) {
            final PyroUserDB user = PyroUserDB.findById(userId);
            if (user == null) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
            }

            user.systemRoles.remove(PyroSystemRoleDB.ORGANIZATION_MANAGER);
            return javax.ws.rs.core.Response.ok(PyroUser.fromEntity(user, objectCache)).build();
        }

        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
    }

    private boolean isAdmin(PyroUserDB user) {
        return user.systemRoles.contains(PyroSystemRoleDB.ADMIN);
    }

    public void deleteUser(PyroUserDB user) {
        java.util.List<PyroOrganizationDB> orgs = PyroOrganizationDB.listAll();
        orgs.forEach((org) -> {
            if (org.owners.contains(user) || org.members.contains(user)) {
                this.organizationController.removeFromOrganization(user, org);
            }
        });
        user.delete();
    }
}
