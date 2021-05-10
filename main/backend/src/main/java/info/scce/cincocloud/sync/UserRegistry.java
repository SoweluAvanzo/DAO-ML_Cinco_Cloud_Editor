package info.scce.cincocloud.sync;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

/**
 * Author zweihoff
 */
@ApplicationScoped
public class UserRegistry extends WebSocketRegistry {
    /**
     * Map<UserId,Map<Websocket>>
     */
    private Map<Long, Session> currentOpenSockets;

    public UserRegistry() {
        currentOpenSockets = new ConcurrentHashMap<>();
    }

    public Map<Long, Session> getCurrentOpenSockets() {
        return currentOpenSockets;
    }

    public void setCurrentOpenSockets(Map<Long, Session> currentOpenSockets) {
        this.currentOpenSockets = currentOpenSockets;
    }

    public void send(long userId, WebSocketMessage message) {
        if (currentOpenSockets.containsKey(userId)) {
            super.send(currentOpenSockets.get(userId), message);
        }
    }
}
