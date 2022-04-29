package info.scce.cincocloud.sync;

import info.scce.cincocloud.core.rest.tos.ProjectDeploymentTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.StopProjectPodsTaskDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.sync.helper.WorkerThreadHelper;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/api/ws/project/{projectId}/{ticket}/private")
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
        new Object[] {session.getId(), message});
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
    exception.printStackTrace();
    LOGGER.log(Level.INFO, "Error for project client: {0}", session.getId());
  }

  private void createStopProjectPodsTask(long projectId) {
    try {
      transactionManager.begin();
      final var task = new StopProjectPodsTaskDB();
      task.setProjectId(projectId);
      StopProjectPodsTaskDB.persist(task);
      transactionManager.commit();
    } catch (Exception e) {
      e.printStackTrace();
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
  }
}
