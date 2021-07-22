package info.scce.cincocloud.mq;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroWorkspaceImageDB;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.sync.WebSocketMessage;

@ApplicationScoped
public class WorkspaceMQConsumer {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceMQConsumer.class.getName());

    @Inject
    ProjectWebSocket projectWebSocket;

    @Transactional
    @Blocking
    @Incoming("workspaces-jobs-results")
    public void process(JsonObject message) {
        LOGGER.log(Level.INFO, "Received message from workspaces.jobs.results: {0}.", new Object[]{message});

        try {
            final var result = DatabindCodec.mapper().readValue(message.toString(), WorkspaceImageBuildResult.class);
            if (!result.success) {
                throw new IllegalStateException("Image for project '" + result.projectId + "' could not be build.");
            }

            final var projectOptional = PyroProjectDB.findByIdOptional(result.projectId);
            if (projectOptional.isEmpty()) {
                throw new EntityNotFoundException("The project with the id '" + result.projectId + "' could not be found.");
            }
            final var project = (PyroProjectDB) projectOptional.get();

            final var existingImageOptional = PyroWorkspaceImageDB.findByImageName(result.image);
            final PyroWorkspaceImageDB image;
            if (existingImageOptional.isPresent()) {
                image = existingImageOptional.get();
                image.updatedAt = Instant.now();
                image.persist();
                LOGGER.log(Level.INFO, "Image {0} updated.", new Object[]{image.toString()});
            } else {
                image = new PyroWorkspaceImageDB();
                image.name = result.image;
                image.imageName = result.image;
                image.imageVersion = "latest";
                image.published = true;
                image.user = project.owner;
                image.persist();
                LOGGER.log(Level.INFO, "Image {0} created.", new Object[]{image.toString()});
            }

            projectWebSocket.send(project.id, WebSocketMessage.fromEntity(project.owner.id, "workspaces:jobs:results", result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
