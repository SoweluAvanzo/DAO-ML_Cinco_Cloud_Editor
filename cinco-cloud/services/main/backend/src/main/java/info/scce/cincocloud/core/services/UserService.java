package info.scce.cincocloud.core.services;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import io.quarkus.security.UnauthorizedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.core.SecurityContext;

@ApplicationScoped
@Transactional
public class UserService {

  @Inject
  OrganizationService organizationService;

  @Inject
  PBKDF2Encoder passwordEncoder;

  public UserDB getOrThrow(long userId) {
    return (UserDB) UserDB.findByIdOptional(userId)
        .orElseThrow(() -> new EntityNotFoundException("Cannot find user."));
  }

  public List<UserDB> getUsers() {
    return UserDB.listAll();
  }

  public List<UserDB> getUserByUsernameOrEmail(String usernameOrEmail) {
    final List<UserDB> result = new ArrayList<>(UserDB.list("username", usernameOrEmail));
    if (result.size() == 0) {
      result.addAll(UserDB.list("email", usernameOrEmail));
    }

    return result;
  }

  public UserDB create(String email, String name, String username, String password) {
    return create(email, name, username, password, new LinkedList<>());
  }

  public UserDB create(String email, String name, String username, String password,
      Collection<UserSystemRole> roles) {
    UserDB user = new UserDB();
    user.email = email;
    user.name = name;
    user.username = username;
    user.password = password;
    Random random = new Random();
    user.activationKey = random.ints(97, 122 + 1)
        .limit(15)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    user.systemRoles = roles;
    user.isActivated = false;
    user.persist();
    return user;
  }

  /**
   * Removes a user from all associated organizations and deletes the user from the database.
   *
   * @param userId The id of the user to delete.
   */
  public void delete(long userId) {
    final var userToDelete = getOrThrow(userId);

    if (!userCanBeDeleted(userToDelete)) {
      throw new IllegalArgumentException("Could not delete user. User still has unresolved responsibilities.");
    }

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
        this.organizationService.removeUserFromOrganization(userToDelete, org);
      }
    });
    UserDB userToDeleteUpdate = UserDB.findById(userToDelete.id);
    if(userToDeleteUpdate != null){
      userToDeleteUpdate.delete();
    }
  }

  public UserDB updateEmail(UserDB user, String email) {
    if (!user.email.equals(email) && UserDB.find("email", email).firstResult() != null) {
      throw new IllegalArgumentException("Email is already taken.");
    }

    user.email = email;
    user.persist();

    return user;
  }

  public UserDB updateName(UserDB user, String name) {
    user.name = name;
    user.persist();

    return user;
  }

  public UserDB updateProfilePicture(UserDB user, Optional<Long> profilePictureIdOptional) {
    if (profilePictureIdOptional.isPresent()) {
      user.profilePicture = BaseFileDB.findById(profilePictureIdOptional.get());
    } else {
      if (user.profilePicture != null) {
        user.profilePicture.delete();
      }
      user.profilePicture = null;
    }
    user.persist();

    return user;
  }

  public UserDB updatePassword(UserDB user, String password) {
    user.password = passwordEncoder.encode(password);
    user.persist();

    return user;
  }

  public UserDB setAdmin(long userId, boolean admin) {
    final var user = (UserDB) getOrThrow(userId);

    if (admin) {
      if (user.systemRoles.contains(UserSystemRole.ADMIN)) {
        user.systemRoles.add(UserSystemRole.ADMIN);
      }
    } else {
      user.systemRoles.remove(UserSystemRole.ADMIN);
    }

    return user;
  }

  public static UserDB getCurrentUser(SecurityContext securityContext) {
    return (UserDB) Optional.ofNullable(UserDB.find("email", securityContext.getUserPrincipal().getName()).firstResult())
        .orElseThrow(() -> new UnauthorizedException("Not logged in."));
  }

  private boolean userCanBeDeleted(UserDB userToDelete) {
    final List<UserDB> result = UserDB.listAll();
    // a user cannot delete their account, if they're the only admin
    if ((userToDelete.isAdmin() && result.stream().filter(UserDB::isAdmin).count() > 1) || !userToDelete.isAdmin()) {
      // a user cannot delete their account, if they own projects with at least one other member
      if (userToDelete.personalProjects.stream().allMatch(project -> project.members.isEmpty())) {
        // a user cannot delete their account, if they are the sole owner of an organization
        return userToDelete.ownedOrganizations.stream().noneMatch(org -> org.owners.size() == 1);
      }
    }
    return false;
  }
}
