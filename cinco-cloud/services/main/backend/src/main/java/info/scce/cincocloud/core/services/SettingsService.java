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
    settings.persist();

    return settings;
  }
}
