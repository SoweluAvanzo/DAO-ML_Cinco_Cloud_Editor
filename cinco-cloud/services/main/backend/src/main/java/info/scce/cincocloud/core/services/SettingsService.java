package info.scce.cincocloud.core.services;

import info.scce.cincocloud.db.SettingsDB;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class SettingsService {

  public SettingsDB getSettings() {
    return SettingsDB.findAll().firstResult();
  }

  public SettingsDB setAllowPublicUserRegistration(boolean allowPublicUserRegistration) {
    final var settings = getSettings();
    settings.allowPublicUserRegistration = allowPublicUserRegistration;

    return settings;
  }

  public SettingsDB setAutoActivateUsers(boolean autoActivateUsers) {
    final var settings = getSettings();
    settings.autoActivateUsers = autoActivateUsers;

    return settings;
  }

  public SettingsDB setSendMails(boolean sendMails) {
    final var settings = getSettings();
    settings.sendMails = sendMails;

    return settings;
  }
}
