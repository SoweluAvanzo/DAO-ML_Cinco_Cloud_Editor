package info.scce.cincocloud.core.beans;

import io.quarkus.scheduler.Scheduled;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RemoveUntaggedImagesTaskBean {

    private static final Logger LOGGER = Logger.getLogger(RemoveUntaggedImagesTaskBean.class.getName());

    @ConfigProperty(name = "podman.registry.host")
    String registryHost;

    @ConfigProperty(name = "podman.registry.api.port")
    Integer registryApiPort;

    @Inject
    Vertx vertx;

    @Transactional
    @Scheduled(every = "1h", identity = "remove-untagged-images-task")
    void schedule() {
        final var webClient = WebClient.create(vertx);
        LOGGER.log(Level.INFO, "Make request to: {0}", new Object[]{getGarbageCollectionUrl()});
        final var request = webClient.post(registryApiPort, registryHost, "/api/registry/gc/run").send();
        request.subscribe().with(req -> {
            LOGGER.log(Level.INFO, "Removed untagged images in registry: {0}", new Object[]{req.bodyAsString()});
        }, err -> {
            LOGGER.log(Level.INFO, "Failed to remove untagged images in registry: {0}", new Object[]{err.getMessage()});
        });
    }

    private String getGarbageCollectionUrl() {
        return "http://" + registryHost + ":" + registryApiPort + "/api/registry/gc/run";
    }
}
