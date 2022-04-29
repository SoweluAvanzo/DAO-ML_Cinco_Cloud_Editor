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
    private final Map<Long, Map<String,Session>> currentOpenSockets;

    public GraphModelRegistry(){
        currentOpenSockets = new ConcurrentHashMap<>();
    }

    public Map<Long, Map<String,Session>> getCurrentOpenSockets() {
        return currentOpenSockets;
    }


    public void send(long graphModelId,WebSocketMessage message){
        if(currentOpenSockets.containsKey(graphModelId)){
            java.util.Set<Map.Entry<String,Session>> sessionEntries = currentOpenSockets.get(graphModelId).entrySet();
            for(Map.Entry<String, Session> entry: sessionEntries) {
            	super.send(entry.getValue(), message);
            }
        }
    }
}
