package info.scce.pyro.sync;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import info.scce.pyro.rest.PyroSelectiveRestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

/**
 * Author zweihoff
 */
@ServerEndpoint(value = "/api/ws/graphmodel/{graphModelId}/private")
@ApplicationScoped
public class GraphModelWebSocket {

    @Inject
    GraphModelRegistry graphModelRegistry;

    @Inject
    DialogRegistry dialogRegistry;
    
    static final String userIdKey = "user_id";

    private static final Logger LOGGER =
            Logger.getLogger(GraphModelWebSocket.class.getName());

    private static Map<String, Long> userIdMap = new HashMap<String, Long>();
    private static long currentUserId = 0;
    
    @OnOpen
    public void open(final Session session,@PathParam("graphModelId") long graphModelId) throws IOException {
    	String userId = session.getId();
    	session.setMaxIdleTimeout(3600000);    	
    	session.getUserProperties().put(userIdKey, userId); // TODO: SAMI: needed?    	
    	graphModelRegistry.getCurrentOpenSockets().putIfAbsent(graphModelId,new ConcurrentHashMap<>());
        graphModelRegistry.getCurrentOpenSockets().get(graphModelId).put(userId,session);
        
        registerUserId(session);
    }
    
    private void registerUserId(Session session) {
    	String alphaUserId = session.getId();
    	Long numUserId = getNextUserId(); 
    	userIdMap.put(alphaUserId, numUserId);
    	
    	WebSocketMessage message = WebSocketMessage.fromEntity(numUserId, "userInformation", null);
    	graphModelRegistry.send(session, message);
    }
    
    private void unregisterUserId(Session session) {
    	String userId = session.getId();
        synchronized(userIdMap) {
        	if(userIdMap.containsKey(userId)) {
            	userIdMap.remove(userId);
        	}
        }
    }
    
    private synchronized long getNextUserId() {
    	long userId = currentUserId;
    	currentUserId = userId + 1;
    	return userId;
    }

    public void send(long graphmodelId,WebSocketMessage message)
    {
        graphModelRegistry.send(graphmodelId,message);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter("PYRO_Selective_Filter", new PyroSelectiveRestFilter()));
        try {
        	final WebSocketMessage m = mapper.readValue(message, WebSocketMessage.class);
        	
        	switch (m.getevent()) {
        		case "dialog":
        			info.scce.pyro.message.DialogAnswer da = mapper.convertValue(m.getcontent(), info.scce.pyro.message.DialogAnswer.class);
        			dialogRegistry.notify(da.getDialogId(),da.getUserId(),da.getAnswer());
        			break;
        		case "updateCursorPosition":
        			final UpdateCursorPosition ucp = mapper.convertValue(m.getcontent(), UpdateCursorPosition.class);
        			graphModelRegistry.send(ucp.getgraphModelId(), WebSocketMessage.fromEntity(m.getsenderId(), "updateCursorPosition", ucp));
        			break;
        		default:
    				break;
        	}            
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "New message from Client [{0}]: {1}",
                new Object[] {session.getId(), message});
    }

    @OnClose
    public void onClose(Session session) {
    	String userId = session.getId();
        unregisterUserId(session);
        this.graphModelRegistry.getCurrentOpenSockets().values().removeIf(n->n.containsKey(userId));
        LOGGER.log(Level.INFO, "Close graphmodel connection for client: {0}",
                session.getId());
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        unregisterUserId(session);
        LOGGER.log(Level.INFO, "Error for graphmodel client: {0}", session.getId());
    }

    public boolean hasGraphModel(long graphModelId)
    {
        return 	this.graphModelRegistry.getCurrentOpenSockets().containsKey(graphModelId)
                && this.graphModelRegistry.getCurrentOpenSockets().get(graphModelId).size() > 0;
    }

    /**
     * Closes connections of all WebSockets, that are
     * listening on currentUser with the given id,
     * because of the deletion.
     *
     * @param id	ID of graphModel.
     */
    public void closeAfterDeletion(long id)
    {
        if(this.hasGraphModel(id))
        {
            this.graphModelRegistry.getCurrentOpenSockets()
                    .get(id).values()
                    .forEach((w) -> {
                        try {
                            System.out.println("[PYRO] Closing WebSocket with code 4000 after deleting graphmodel" + id + ".");
                            graphModelRegistry.close(w,4000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    });
            this.graphModelRegistry.getCurrentOpenSockets().remove(id);
        }
    }

    /**
     * Closes all open sockets to users, not contained in the allowedUsersList
     * for the given graphModel
     * @param graphModelId ID of the graphmodel
     * @param allowedUserList IDs of the allowed users for the graphmodel
     */
    public void updateUserList(long graphModelId, List<String> allowedUserList){
        if(this.hasGraphModel(graphModelId))
        {
            Stream<Map.Entry<String, Session>> socketsToClose = this.graphModelRegistry.getCurrentOpenSockets()
                    .get(graphModelId).entrySet().stream().filter(n->!allowedUserList.contains(n.getKey()));
            socketsToClose.forEach((w) -> {
                try {
                    System.out.println("[PYRO] Closing WebSocket with code 4000 after deleting user from allowed list.");
                    graphModelRegistry.close(w.getValue(),4001);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                this.graphModelRegistry.getCurrentOpenSockets().get(graphModelId).remove(w.getKey());
            });
        }
    }

    public DialogRegistry getDialogRegistry() {
        return dialogRegistry;
    }

    public void setDialogRegistry(DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
    }
}
