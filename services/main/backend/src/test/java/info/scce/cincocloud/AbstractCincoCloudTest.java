package info.scce.cincocloud;

import info.scce.cincocloud.core.RegistrationService;
import info.scce.cincocloud.core.UserService;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import io.quarkus.test.common.QuarkusTestResource;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.gradle.internal.service.scopes.Scopes.Project;

@QuarkusTestResource(PostgresResource.class)
@QuarkusTestResource(ArtemisResource.class)
public abstract class AbstractCincoCloudTest {

  @Inject
  protected RegistrationService registrationService;

  @Inject
  protected UserService userService;

  protected final Map<String, String> defaultHeaders = Map.of("content-type", "application/json");

  @Transactional
  public void reset() {
    final var settings = (SettingsDB) SettingsDB.findAll().list().get(0);
    settings.allowPublicUserRegistration = true;
    settings.persist();

    ProjectDB.deleteAll();

    UserDB.findAll().list().stream()
        .map(u -> (UserDB) u)
        .filter(u -> !u.systemRoles.contains(UserSystemRole.ADMIN))
        .forEach(u -> userService.deleteUser(u));

    // create an admin account
    if (UserDB.findAll().count() == 0) {
      final var admin = new UserRegistrationInput();
      admin.setEmail("admin@cincocloud");
      admin.setName("admin");
      admin.setUsername("admin");
      admin.setPassword("123456");
      admin.setPasswordConfirm("123456");
      registrationService.registerUser(admin);
    }
  }

  protected Map<String, String> getAuthHeaders(String jwt) {
    final var headers = new HashMap<>(defaultHeaders);
    headers.put("Authorization", "Bearer " + jwt);
    return headers;
  }
}
