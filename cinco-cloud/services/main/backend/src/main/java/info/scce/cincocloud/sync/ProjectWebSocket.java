package info.scce.cincocloud.sync;

import info.scce.cincocloud.core.rest.tos.ProjectDeploymentTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuilderLogMessageTO;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.sync.helper.WorkerThreadHelper;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionManager;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/api/ws/project/{projectId}/{ticket}")
@ApplicationScoped
public class ProjectWebSocket {

  private static final String PROJECT_ID_KEY = "project_id";

  private static final Logger LOGGER = Logger.getLogger(ProjectWebSocket.class.getName());

  @Inject
  ProjectRegistry projectRegistry;

  @Inject
  TransactionManager transactionManager;

  @OnOpen
  public void open(
      final Session session,
      @PathParam("projectId") final long projectId,
      @PathParam("ticket") final String ticket
  ) throws IOException {
    final UserDB user = TicketRegistrationHandler.checkGetRelated(ticket);
    if (user == null) {
      // no valid ticket
      session.close();
      return;
    }

    session.getUserProperties().put(PROJECT_ID_KEY, projectId);
    projectRegistry.addSession(projectId, session);
  }

  public void send(long projectId, WebSocketMessage message) {
    projectRegistry.send(projectId, message);
  }

  @OnMessage
  public void onMessage(String message, Session session) {
    LOGGER.log(Level.INFO, "Message from client [{0}]: {1}",
        new Object[]{session.getId(), message});
  }

  @OnClose
  public void onClose(Session session) {
    long projectId = (long) session.getUserProperties().get(PROJECT_ID_KEY);
    projectRegistry.removeSession(projectId, session);

    if (projectRegistry.getSessions(projectId).isEmpty()) {
      WorkerThreadHelper.runWorkerThread(() -> createStopProjectPodsTask(projectId));
    }

    LOGGER.log(Level.INFO, "Close project connection for client: {0}", session.getId());
  }

  @OnError
  public void onError(Throwable exception, Session session) {
    LOGGER.log(Level.INFO, "Error for project client: " + session.getId(), exception);
  }

  private void createStopProjectPodsTask(long projectId) {
    try {
      final var task = new StopProjectPodsTaskDB();
      task.setProjectId(projectId);

      if (transactionManager.getTransaction() == null) {
        transactionManager.begin();
        StopProjectPodsTaskDB.persist(task);
        transactionManager.commit();
      } else {
        StopProjectPodsTaskDB.persist(task);
      }
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "Failed to create task to stop project.", e);
    }
  }

  public static class Messages {

    public static WebSocketMessage podDeploymentStatus(ProjectDeploymentTO deployment) {
      return WebSocketMessage.fromEntity(-1, "project:podDeploymentStatus", deployment);
    }

    public static WebSocketMessage updateBuildJobStatus(WorkspaceImageBuildJobTO buildJob) {
      return WebSocketMessage.fromEntity(
          -1,
          "project:buildJobs:updateStatus",
          buildJob
      );
    }

    public static WebSocketMessage workspaceImageBuilderLogMessage(WorkspaceImageBuilderLogMessageTO logMessage) {
      return WebSocketMessage.fromEntity(
          -1,
          "project:buildJobs:logMessage",
          logMessage
      );
    }
  }
}
