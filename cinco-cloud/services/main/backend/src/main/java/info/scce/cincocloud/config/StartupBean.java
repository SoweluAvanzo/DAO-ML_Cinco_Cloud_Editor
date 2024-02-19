package info.scce.cincocloud.config;

import com.google.common.base.Strings;
import info.scce.cincocloud.core.services.SettingsService;
import info.scce.cincocloud.db.SettingsDB;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Startup
public class StartupBean {

  private static final Logger LOGGER = Logger.getLogger(StartupBean.class.getName());

  @Inject
  Properties properties;

  @Inject
  SettingsService settingsService;

  @Transactional
  public void startup(@Observes StartupEvent event) throws Exception {
    initSettings();
  }

  private void initSettings() {
    LOGGER.log(Level.INFO, "Init application settings.");

    if (SettingsDB.listAll().isEmpty()) {
      final var settings = new SettingsDB();
      settings.allowPublicUserRegistration = true;
      settings.sendMails = false;
      settings.archetypeImage = properties.getArchetypeImage()
              .orElse("registry.gitlab.com/scce/cinco-cloud/archetype:latest");
      settings.persist();
    } else {
      // update archetype image if environment variable has been set externally
      final var settings = settingsService.getSettings();
      settings.archetypeImage = properties.getArchetypeImage().orElse(settings.archetypeImage);
      settings.persist();
    }
  }
}
