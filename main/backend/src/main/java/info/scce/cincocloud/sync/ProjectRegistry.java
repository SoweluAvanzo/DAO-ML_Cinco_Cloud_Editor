package info.scce.cincocloud.sync;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

/**
 * Author zweihoff
 */
@ApplicationScoped
public class ProjectRegistry extends WebSocketRegistry {
    /**
     * Map<ProjectId,Map<UserId,Session>>
     */
    private final Map<Long, Map<Long, Session>> currentOpenSockets;

    public ProjectRegistry() {
        currentOpenSockets = new ConcurrentHashMap<>();
    }

    public Map<Long, Map<Long, Session>> getCurrentOpenSockets() {
        return currentOpenSockets;
    }


    public void send(long projectId, WebSocketMessage message) {
        if (currentOpenSockets.containsKey(projectId)) {
            currentOpenSockets.get(projectId).entrySet().forEach(n -> super.send(n.getValue(), message));
        }
    }
}
