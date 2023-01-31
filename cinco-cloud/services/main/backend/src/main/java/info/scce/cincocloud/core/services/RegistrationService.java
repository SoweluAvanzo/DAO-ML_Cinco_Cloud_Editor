package info.scce.cincocloud.core.services;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class RegistrationService {

  @Inject
  PBKDF2Encoder passwordEncoder;

  @Inject
  UserService userService;

  public UserDB registerUser(String name, String username, String email, String password) {

    if (!UserDB.list("username", username).isEmpty() || !OrganizationDB.list("name", username).isEmpty()) {
      throw new IllegalArgumentException("The username already exists.");
    }
    if (!UserDB.list("email", email).isEmpty()) {
      throw new IllegalArgumentException("The email already exists.");
    }

    final var user = userService.create(email, name, username, passwordEncoder.encode(password));

    if (UserDB.count() == 1) {
      user.systemRoles.add(UserSystemRole.ADMIN);
    }

    // TODO: SAMI: send activation mail
    // TODO: SAMI: remove this later (for development use)
    user.isActivated = true;
    user.persist();
    return user;
  }
}
