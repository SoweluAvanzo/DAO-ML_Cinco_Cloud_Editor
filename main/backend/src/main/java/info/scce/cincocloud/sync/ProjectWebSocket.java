package info.scce.cincocloud.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import info.scce.cincocloud.core.rest.types.PyroUser;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.sync.helper.WorkerThreadHelper;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;

@ServerEndpoint(value = "/api/ws/project/{projectId}/{ticket}/private")
@ApplicationScoped
public class ProjectWebSocket {

    static final String userIdKey = "user_id";
    private static final Logger LOGGER = Logger.getLogger(ProjectWebSocket.class.getName());

    @Inject
    ProjectRegistry projectRegistry;

    @Inject
    ObjectCache objectCache;

    @OnOpen // NOTE: rewritten with ticket-system, since Session.getUserPrincipal does only work with cookies
    public void open(final Session session, @PathParam("projectId") long clientId, @PathParam("ticket") String ticket) throws IOException {
        final PyroUserDB user = TicketRegistrationHandler.checkGetRelated(ticket);
        if (user == null) {
            // no valid ticket
            session.close();
            return;
        }
        session.getUserProperties().put(userIdKey, user.id);

        projectRegistry.getCurrentOpenSockets().putIfAbsent(clientId, new HashMap<>());
        projectRegistry.getCurrentOpenSockets().get(clientId).put(user.id, session);

        final List<PyroUser> users = new ArrayList<>();

        // NOTE: added
        WorkerThreadHelper.runWorkerThread(() -> {
            projectRegistry.getCurrentOpenSockets().get(clientId).forEach((key, value) -> {
                try {
                    final PyroUserDB u = PyroUserDB.findById(key);
                    users.add(PyroUser.fromEntity(u, objectCache));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            projectRegistry.send(clientId, WebSocketMessage.fromEntity(user.id, "project:updateUserList", users));
        });
    }

    public void send(long projectId, WebSocketMessage message) {
        projectRegistry.send(projectId, message);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.log(Level.INFO, "New message from Client [{0}]: {1}", new Object[]{session.getId(), message});
    }

    @OnClose
    public void onClose(Session session) {
        long userId = (long) session.getUserProperties().get(userIdKey);
        this.projectRegistry.getCurrentOpenSockets().forEach((projectId, sessionMap) -> {
            if (sessionMap.containsKey(userId)) {
                projectRegistry.send(projectId, WebSocketMessage.fromEntity(userId, "project:removeUser", userId));
                sessionMap.remove(userId);
            }
        });

        LOGGER.log(Level.INFO, "Close project connection for client: {0}", session.getId());
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        exception.printStackTrace();
        LOGGER.log(Level.INFO, "Error for project client: {0}", session.getId());
    }

    /**
     * Closes all open sockets to users, not contained in the allowedUsersList
     * for the given project
     *
     * @param projectId
     *         ID of the project
     * @param allowedUserList
     *         IDs of the allowed users for the project
     */
    public void updateUserList(long projectId, List<Long> allowedUserList) {
        if (this.projectRegistry.getCurrentOpenSockets().containsKey(projectId)) {
            this.projectRegistry.getCurrentOpenSockets().get(projectId).entrySet().stream()
                    .filter(n -> !allowedUserList.contains(n.getKey()))
                    .forEach((w) -> {
                        try {
                            projectRegistry.close(w.getValue(), 4001);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        this.projectRegistry.getCurrentOpenSockets().get(projectId).remove(w.getKey());
                    });
        }
    }
}
