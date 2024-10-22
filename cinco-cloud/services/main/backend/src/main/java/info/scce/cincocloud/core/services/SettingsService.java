package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.tos.SettingsTO;
import info.scce.cincocloud.db.SettingsDB;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class SettingsService {

  public SettingsDB getSettings() {
    return SettingsDB.findAll().firstResult();
  }

  public SettingsDB updateSettings(SettingsTO settingsTO) {
    final var settings = getSettings();
    settings.allowPublicUserRegistration = settingsTO.getallowPublicUserRegistration();
    settings.autoActivateUsers = settingsTO.getautoActivateUsers();
    settings.sendMails = settingsTO.getsendMails();
    settings.persistentDeployments = settingsTO.getpersistentDeployments();
    settings.archetypeImage = settingsTO.getArchetypeImage();
    settings.createDefaultProjects = settingsTO.isCreateDefaultProjects();
    return settings;
  }
}
