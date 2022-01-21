package info.scce.cincocloud.core;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.exeptions.RestException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

@ApplicationScoped
@Transactional
public class RegistrationService {

  @Inject
  PBKDF2Encoder passwordEncoder;

  public UserDB registerUser(UserRegistrationInput userRegistration) {
    final var settings = (SettingsDB) SettingsDB.findAll().list().get(0);
    if (!settings.allowPublicUserRegistration) {
      throw new RestException(Status.FORBIDDEN, "User registration is currently disabled");
    }

    final var emailExists = !UserDB.list("email", userRegistration.getEmail()).isEmpty();
    if (emailExists) {
      throw new RestException("The email already exists");
    }

    final var passwordsAreNotEqual = !userRegistration.getPassword().equals(userRegistration.getPasswordConfirm());
    if (passwordsAreNotEqual) {
      throw new RestException("The passwords to not match");
    }

    final var user = UserDB.add(
        userRegistration.getEmail(),
        userRegistration.getName(),
        userRegistration.getUsername(),
        passwordEncoder.encode(userRegistration.getPassword())
    );

    if (UserDB.count() == 1) {
      user.systemRoles.add(UserSystemRole.ADMIN);
      user.systemRoles.add(UserSystemRole.ORGANIZATION_MANAGER);
    }

    user.persist();

    // TODO: SAMI: send activation mail
    // TODO: SAMI: remove this later (for development use)
    user.isActivated = true;

    return user;
  }
}
