package info.scce.cincocloud.config;

import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.grpc.MainServiceGrpcImpl;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Startup
public class StartupBean {

  private static final Logger LOGGER = Logger.getLogger(StartupBean.class.getName());

  @ConfigProperty(name = "cincocloud.data.dir")
  String dataDirectory;

  @Inject
  MainServiceGrpcImpl mainServiceGrpc;

  @Transactional
  public void startup(@Observes StartupEvent event) throws Exception {
    initDataDirectory();
    initSettings();
  }

  private void initDataDirectory() throws IOException {
    final var dir = Path.of(dataDirectory);
    LOGGER.log(Level.INFO, "Init directory: " + dir.toAbsolutePath());
    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
    }
  }

  private void initSettings() {
    LOGGER.log(Level.INFO, "Init application settings.");
    if (SettingsDB.listAll().isEmpty()) {
      final var settings = new SettingsDB();
      settings.allowPublicUserRegistration = true;
      settings.persist();
    }
  }
}
