package info.scce.cincocloud.mq;

import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.core.services.WorkspaceImageBuildJobLogFileService;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.sync.ProjectWebSocket.Messages;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class WorkspaceMQConsumer {

  private static final Logger LOGGER = Logger.getLogger(WorkspaceMQConsumer.class.getName());

  @Inject
  ProjectWebSocket projectWebSocket;

  @Inject
  WorkspaceImageBuildJobLogFileService logFileService;

  @Transactional
  @Blocking
  @Incoming("workspaces-jobs-results")
  public void process(JsonObject message) {
    LOGGER.log(Level.INFO, "Received message from workspaces.jobs.results: {0}.",
        new Object[]{message});

    WorkspaceImageBuildJobDB buildJob = null;

    try {
      final var result = message.mapTo(WorkspaceImageBuildResultMessage.class);

      buildJob = (WorkspaceImageBuildJobDB) WorkspaceImageBuildJobDB
          .findByIdOptional(result.jobId)
          .orElseThrow(() -> new EntityNotFoundException(
              "The job with the id '" + result.jobId + "' could not be found."));

      // finalize log transmission
      logFileService.finalizeTransmission(buildJob.id, buildJob.project.id);

      buildJob.status = result.success
          ? WorkspaceImageBuildJobDB.Status.FINISHED_WITH_SUCCESS
          : WorkspaceImageBuildJobDB.Status.FINISHED_WITH_FAILURE;
      buildJob.finishedAt = Instant.now();
      buildJob.persist();

      if (!result.success) {
        throw new IllegalStateException(
            "Image for project '" + result.projectId + "' could not be build.");
      }

      final var project = (ProjectDB) ProjectDB.findByIdOptional(result.projectId)
          .orElseThrow(() -> new EntityNotFoundException(
              "The project with the id '" + result.projectId + "' could not be found."));

      final var existingImageOptional = WorkspaceImageDB.findByUUID(result.uuid);
      final WorkspaceImageDB image;
      if (existingImageOptional.isPresent()) {
        image = existingImageOptional.get();
        image.updatedAt = Instant.now();
        image.persist();
        LOGGER.log(Level.INFO, "Image {0} updated.", new Object[]{image.toString()});
      } else {
        image = new WorkspaceImageDB();
        image.uuid = result.uuid;
        image.imageVersion = "latest";
        image.published = false;
        image.project = project;
        image.persist();

        project.image = image;
        project.persist();

        LOGGER.log(Level.INFO, "Image {0} created.", new Object[]{image.toString()});
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (buildJob != null) {
        final var objectCache = new ObjectCache();
        final var buildJobTo = WorkspaceImageBuildJobTO.fromEntity(buildJob, objectCache);
        projectWebSocket.send(buildJob.project.id, Messages.updateBuildJobStatus(buildJobTo));
      }
    }
  }
}
