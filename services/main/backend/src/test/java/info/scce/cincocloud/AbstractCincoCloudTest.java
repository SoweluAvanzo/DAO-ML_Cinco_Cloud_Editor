package info.scce.cincocloud;

import info.scce.cincocloud.core.RegistrationService;
import info.scce.cincocloud.core.UserService;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import io.quarkus.test.common.QuarkusTestResource;
import javax.inject.Inject;
import javax.transaction.Transactional;

@QuarkusTestResource(PostgresResource.class)
@QuarkusTestResource(ArtemisResource.class)
public abstract class AbstractCincoCloudTest {

  @Inject
  RegistrationService registrationService;

  @Inject
  UserService userService;

  @Transactional
  protected void reset() {
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
}
