package info.scce.cincocloud;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.services.OrganizationService;
import info.scce.cincocloud.core.services.RegistrationService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import io.quarkus.test.common.QuarkusTestResource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTestResource(PostgresResource.class)
public abstract class AbstractCincoCloudTest {

  @Inject
  PBKDF2Encoder passwordEncoder;

  @Inject
  protected RegistrationService registrationService;

  @Inject
  protected OrganizationService organizationService;

  @Inject
  protected UserService userService;

  protected final Map<String, String> defaultHeaders = Map.of("content-type", "application/json");

  @Transactional
  public void reset() {
    final List<SettingsDB> allSettings = SettingsDB.findAll().list();
    final SettingsDB settings;
    if (allSettings.isEmpty()) {
      settings = new SettingsDB();
    } else {
      settings = allSettings.get(0);
      settings.allowPublicUserRegistration = true;
      settings.sendMails = true;
      settings.autoActivateUsers = false;
    }
    settings.persist();

    OrganizationDB.listAll().stream()
        .map(organization -> (OrganizationDB) organization)
        .forEach(organization -> organizationService.delete(organization));

    ProjectDB.deleteAll();

    UserDB.listAll().stream()
        .map(user -> (UserDB) user)
        .filter(user -> !user.systemRoles.contains(UserSystemRole.ADMIN))
        .forEach(user -> userService.delete(user.id));

    // create an admin account
    if (UserDB.findAll().count() == 0) {
      registrationService.registerUser("admin", "admin", "admin@cincocloud", "123456");
    }
  }

  protected Map<String, String> getAuthHeaders(String jwt) {
    final var headers = new HashMap<>(defaultHeaders);
    headers.put("Authorization", "Bearer " + jwt);
    return headers;
  }

  @Transactional
  public UserDB createAndActivateUser(String name, String username, String email, String password) {
    final var user = userService.create(email, name, username, passwordEncoder.encode(password));
    userService.activateUser(user, false);

    return user;
  }
}
