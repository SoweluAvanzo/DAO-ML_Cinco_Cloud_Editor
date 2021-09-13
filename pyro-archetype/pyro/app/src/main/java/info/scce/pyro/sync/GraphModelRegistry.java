package info.scce.pyro.sync;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

/**
 * Author zweihoff
 */
@ApplicationScoped
public class GraphModelRegistry extends WebSocketRegistry {
    /**
     * Map<GraphModel,Map<UserId,Session>>
     */
    private final Map<Long, Map<Long,Session>> currentOpenSockets;

    public GraphModelRegistry(){
        currentOpenSockets = new ConcurrentHashMap<>();
    }

    public Map<Long, Map<Long,Session>> getCurrentOpenSockets() {
        return currentOpenSockets;
    }


    public void send(long graphModelId,WebSocketMessage message){
        if(currentOpenSockets.containsKey(graphModelId)){
            currentOpenSockets.get(graphModelId).entrySet().forEach(n->super.send(n.getValue(),message));
        }
    }
}
