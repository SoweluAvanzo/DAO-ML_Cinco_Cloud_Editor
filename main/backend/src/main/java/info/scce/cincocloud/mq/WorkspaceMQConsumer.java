package info.scce.cincocloud.mq;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroWorkspaceImageDB;

@ApplicationScoped
public class WorkspaceMQConsumer {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceMQConsumer.class.getName());

    @Transactional
    @Incoming("workspaces-jobs-results")
    public void process(JsonObject message) {
        LOGGER.log(Level.INFO, "Received message from workspaces.jobs.results: {0}.", new Object[]{message});

        try {
            final var result =  DatabindCodec.mapper().readValue(message.toString(), WorkspaceImageBuildResult.class);

            if (!result.success) {
                throw new IllegalStateException("Image for project '" + result.projectId + "' could not be build.");
            }

            final var projectOptional = PyroProjectDB.findByIdOptional(result.projectId);
            if (projectOptional.isEmpty()) {
                throw new EntityNotFoundException("The project with the id '" + result.projectId + "' could not be found.");
            }

            final var project = (PyroProjectDB) projectOptional.get();

            final var image = new PyroWorkspaceImageDB();
            image.imageName = result.image;
            image.imageVersion = "latest";
            image.published = false;
            image.user = project.owner;
            image.persist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
