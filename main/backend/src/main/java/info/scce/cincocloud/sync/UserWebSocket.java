package info.scce.cincocloud.sync;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;

/**
 * Author zweihoff
 */
@ServerEndpoint(value = "/ws/user/{ticket}/private")
@ApplicationScoped
public class UserWebSocket {

    static final String userIdKey = "user_id";
    private static final Logger LOGGER =
            Logger.getLogger(UserWebSocket.class.getName());
    @Inject
    UserRegistry userRegistry;

    @OnOpen // NOTE: rewritten with ticket-system, since Session.getUserPrincipal does only work with cookies
    public void open(final Session session, EndpointConfig conf, @PathParam("ticket") String ticket) throws IOException {
        session.setMaxIdleTimeout(3600000);

        final PyroUserDB user = TicketRegistrationHandler.checkGetRelated(ticket);
        if (user == null) {
            // no valid ticket
            session.close();
            return;
        }
        session.getUserProperties().put(userIdKey, user.id);
        userRegistry.getCurrentOpenSockets().put(user.id, session);
    }

    /**
     * Sends the given serialized instance of Project to all
     * listening WebSocket connections for currentUser with the
     * given id.
     *
     * @param message
     *         Serialized currentUser.
     */
    public void send(long receiverId, WebSocketMessage message) {
        userRegistry.send(receiverId, message);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.log(Level.INFO, "New message from Client [{0}]: {1}",
                new Object[]{session.getId(), message});
    }

    @OnClose
    public void onClose(Session session) {
        this.userRegistry.getCurrentOpenSockets().values().remove(session);
        LOGGER.log(Level.INFO, "Close connection for client: {0}",
                session.getId());
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        LOGGER.log(Level.INFO, "Error for client: {0}", session.getId());
    }
}
