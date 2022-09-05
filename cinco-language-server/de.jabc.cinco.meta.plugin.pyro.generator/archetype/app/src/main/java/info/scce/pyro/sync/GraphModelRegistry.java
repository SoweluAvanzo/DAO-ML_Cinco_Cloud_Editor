package info.scce.pyro.sync;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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


    public void send(long graphModelId , WebSocketMessage message, ReceiverType receiver){
        final String senderId = String.valueOf(message.getsenderId());
        if(currentOpenSockets.containsKey(graphModelId)){
        	Set<Map.Entry<String,Session>> allReceivers = currentOpenSockets.get(graphModelId).entrySet();
            switch(receiver) {
	            case SENDER:
	            		Session senderSession = currentOpenSockets.get(graphModelId).get(senderId);
	                	super.send(senderSession, message);
	                    break;
	            case OTHERS:
	                Set<Map.Entry<String,Session>> receivers = allReceivers.stream().filter(
	                		(Map.Entry<String,Session> entry) -> !entry.getKey().equals(senderId)
	                	).collect(Collectors.toSet());
	                for(Map.Entry<String, Session> entry: receivers) {
	                    super.send(entry.getValue(), message);
	                }
	                break;
	            case ALL:
	            	for(Map.Entry<String, Session> entry: allReceivers) {
	                    super.send(entry.getValue(), message);
	                }
	                break;
	            default:
	                break;
            }
        }
    }
}
