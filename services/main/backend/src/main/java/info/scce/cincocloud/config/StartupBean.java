package info.scce.cincocloud.config;

import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;
import info.scce.cincocloud.db.StyleDB;
import info.scce.cincocloud.grpc.MainServiceGrpcImpl;
import info.scce.cincocloud.k8s.K8SClientService;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
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

  @Inject
  K8SClientService clientService;

  @Transactional
  public void startup(@Observes StartupEvent event) throws Exception {
    initDataDirectory();
    initSettings();
    removeDanglingPods();
  }

  private void initDataDirectory() throws IOException {
    final var dir = Path.of(dataDirectory);
    LOGGER.log(Level.INFO, "Init directory: " + dir.toAbsolutePath().toString());
    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
    }
  }

  private void initSettings() {
    LOGGER.log(Level.INFO, "Init application settings.");
    if (StyleDB.listAll().isEmpty()) {
      StyleDB style = new StyleDB();
      style.navBgColor = "525252";
      style.navTextColor = "afafaf";
      style.bodyBgColor = "313131";
      style.bodyTextColor = "ffffff";
      style.primaryBgColor = "007bff";
      style.primaryTextColor = "ffffff";
      style.persist();

      SettingsDB settings = new SettingsDB();
      settings.style = style;
      settings.persist();
    }
  }

  private void removeDanglingPods() {
    LOGGER.log(Level.INFO, "Remove dangling pods.");
    final KubernetesClient client = clientService.createClient();
    client.apps().statefulSets().list().getItems().stream()
        .filter(s -> s.getMetadata().getName().startsWith("project"))
        .map(s -> s.getMetadata().getLabels().get("project"))
        .filter(Objects::nonNull)
        .map(Long::valueOf)
        .forEach(id -> {
          final var task = new StopProjectPodsTaskDB();
          task.setProjectId(id);
          task.setCreatedAt(Instant.now());
          task.persist();
        });
  }
}
