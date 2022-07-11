package info.scce.cincocloud.core;

import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

@ApplicationScoped
@Transactional
public class UserService {

  @Inject
  OrganizationController organizationController;

  /**
   * Removes a user from all associated organizations and deletes the user from the database.
   *
   * @param userToDelete The user to delete.
   */
  public void deleteUser(UserDB userToDelete) {
    final List<OrganizationDB> organizations = OrganizationDB.listAll();
    organizations.forEach((org) -> {
      if (org.owners.contains(userToDelete) || org.members.contains(userToDelete)) {
        this.organizationController.removeFromOrganization(userToDelete, org);
      }
    });
    userToDelete.delete();
  }

  public void checkIfUserExists(UserDB user) {
    if (user == null) {
      throw new RestException(Status.NOT_FOUND, "user can not be found");
    }
  }
}
