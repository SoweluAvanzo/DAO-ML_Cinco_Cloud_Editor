package info.scce.cincocloud.core.services;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.quarkus.security.UnauthorizedException;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @Inject
  ProjectService projectService;

  @Inject
  SettingsService settingsService;

  @Inject
  Mailer mailer;

  @Inject
  Template registrationMail;

  @Inject
  RegistrationService registrationService;

  public UserDB getOrThrow(long userId) {
    return (UserDB) UserDB.findByIdOptional(userId)
        .orElseThrow(() -> new EntityNotFoundException("Cannot find user."));
  }

  public PanacheQuery<UserDB> getUsers(Optional<UserSystemRole> systemRole) {
    if (systemRole.isPresent()) {
      final var queryString = "select u from UserDB u where ?1 member of u.systemRoles";
      return UserDB.find(queryString, systemRole.get());
    } else {
      return UserDB.findAll();
    }
  }

  public PanacheQuery<UserDB> searchUsers( String usernameOrEmail, Optional<UserSystemRole> systemRole) {
    final var baseQuery = "select u from UserDB u where LOWER(username) LIKE ?1 or LOWER(email) LIKE ?1";
    final var searchString = "%" + usernameOrEmail.toLowerCase() + "%";
    if (systemRole.isPresent()) {
      final var queryString = baseQuery + " and ?2 member of u.systemRoles";
      return UserDB.find(queryString, searchString, systemRole.get());
    } else {
      return UserDB.find(baseQuery, searchString);
    }
  }

  public UserDB create(String email, String name, String username, String password) {
    return create(email, name, username, password, new LinkedList<>());
  }

  public UserDB create(String email, String name, String username, String password,
      Collection<UserSystemRole> roles) {

    if (!UserDB.list("username", username).isEmpty() || !OrganizationDB.list("name", username).isEmpty()) {
      throw new IllegalArgumentException("An account with the username already exists.");
    }
    if (!UserDB.list("email", email).isEmpty()) {
      throw new IllegalArgumentException("An account with the email already exists.");
    }

    final var user = new UserDB();
    user.email = email;
    user.name = name;
    user.username = username;
    user.password = password;
    user.activationKey = UUID.randomUUID().toString();
    user.systemRoles = roles;
    user.isActivated = false;
    user.persist();

    // create default projects for created user
    projectService.createDefaultProjects(user);

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

  public UserDB activateUser(UserDB user, boolean sendMail) {
    user.isActivated = true;

    if (sendMail && settingsService.getSettings().sendMails) {
      final var email = registrationMail.data("url", registrationService.getCincoCloudPath()).render();
      mailer.send(Mail.withHtml(user.email, "Cinco Cloud: You have been registered.", email));
    }

    return user;
  }

  public UserDB deactivateUser(UserDB user) {
    user.isActivated = false;
    user.isDeactivatedByAdmin = true;

    return user;
  }

  public static Optional<UserDB> getCurrentUserOptional(SecurityContext securityContext) {
    return Optional.ofNullable(securityContext.getUserPrincipal())
            .map(Principal::getName)
            .map(name -> UserDB.find("email", name).firstResult());
  }

  public static UserDB getCurrentUser(SecurityContext securityContext) {
    return getCurrentUserOptional(securityContext).orElseThrow(() -> new UnauthorizedException("Not logged in."));
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
