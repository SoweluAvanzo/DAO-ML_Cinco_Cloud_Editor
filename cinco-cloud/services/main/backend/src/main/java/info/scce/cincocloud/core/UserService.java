package info.scce.cincocloud.core;

import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import java.util.List;
import java.util.stream.Collectors;
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
    List<ProjectDB> allProjects = ProjectDB.listAll();
    allProjects.stream()
        .filter(pro -> pro.members.contains(userToDelete))
        .forEach(pro -> {
          pro.members.remove(userToDelete);
          pro.persist();
        });
    if(!userToDelete.personalProjects.isEmpty()){
      userToDelete.personalProjects.forEach(pro -> {
        pro.owner = null;
        pro.persist();
        pro.delete();
      });
    }
    final List<OrganizationDB> organizations = OrganizationDB.listAll();
    organizations.forEach((org) -> {
      if (org.owners.contains(userToDelete) || org.members.contains(userToDelete)) {
        this.organizationController.removeFromOrganization(userToDelete, org);
      }
    });
    UserDB userToDeleteUpdate = UserDB.findById(userToDelete.id);
    if(userToDeleteUpdate != null){
      userToDeleteUpdate.delete();
    }
  }

  public void checkIfUserExists(UserDB user) {
    if (user == null) {
      throw new RestException(Status.NOT_FOUND, "user can not be found");
    }
  }
}
