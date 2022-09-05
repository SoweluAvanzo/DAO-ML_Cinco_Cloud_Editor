package info.scce.pyro.sync;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import entity.core.PyroUserDB;
import info.scce.pyro.auth.PyroUserRegistry;
import info.scce.pyro.rest.PyroSelectiveRestFilter;
import info.scce.pyro.sync.ticket.TicketRegistrationHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

import org.jboss.logging.Logger;

/**
 * Author zweihoff
 */
@ServerEndpoint(value = "/api/ws/graphmodel/{graphModelId}/{ticket}/private")
@ApplicationScoped
public class GraphModelWebSocket {

    private final static String USER_ID = "USER_ID";

    @Inject
    GraphModelRegistry graphModelRegistry;

    @Inject
    DialogRegistry dialogRegistry;

    private static final Logger LOGGER = Logger.getLogger(GraphModelWebSocket.class.getName());
    
    @OnOpen
    public void open(final Session session,@PathParam("graphModelId") long graphModelId, @PathParam("ticket") String ticket) throws IOException {
    	session.setMaxIdleTimeout(3600000);
    	String userId = getUserId(ticket);
    	session.getUserProperties().put(USER_ID, userId);
		graphModelRegistry.getCurrentOpenSockets().putIfAbsent(graphModelId,new ConcurrentHashMap<>());
		graphModelRegistry.getCurrentOpenSockets().get(graphModelId).put(userId,session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    	// configure mapping
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter("PYRO_Selective_Filter", new PyroSelectiveRestFilter()));
        // handle message
        try {
        	final WebSocketMessage m = mapper.readValue(message, WebSocketMessage.class);
        	
        	switch (m.getevent()) {
        		case "dialog":
        			info.scce.pyro.message.DialogAnswer da = mapper.convertValue(m.getcontent(), info.scce.pyro.message.DialogAnswer.class);
        			dialogRegistry.notify(da.getDialogId(),da.getUserId(),da.getAnswer());
        			break;
        		case "updateCursorPosition":
        			final UpdateCursorPosition ucp = mapper.convertValue(m.getcontent(), UpdateCursorPosition.class);
        			String id = ""+m.getsenderId();
        			PyroUserDB user = PyroUserRegistry.getUser(id);
        			ucp.setUserName(user.username);
        			this.send(
        					ucp.getgraphModelId(), 
        					WebSocketMessage.fromEntity(m.getsenderId(), "updateCursorPosition", ucp),
                            ReceiverType.OTHERS
        				);
        			break;
        		default:
    				break;
        	}            
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("New message from Client " + session.getId() + ": " + message);
    }
    
    @OnClose
    public void onClose(Session session) {
    	String userId = session.getUserProperties().getOrDefault(USER_ID, null).toString();
        PyroUserRegistry.removeUser(userId);
        this.graphModelRegistry.getCurrentOpenSockets().values().removeIf(n->n.containsKey(userId));
        LOGGER.info("Close graphmodel connection for client: " + userId);
    }

    @OnError
    public void onError(Throwable exception, Session session) {
    	String userId = session.getUserProperties().getOrDefault(USER_ID, null).toString();
    	PyroUserRegistry.removeUser(userId);
    	LOGGER.info("Error for graphmodel client: " + userId);
    }

    public void send(long graphmodelId,WebSocketMessage message)
    {
    	send(graphmodelId, message, ReceiverType.OTHERS);
    }

    public void send(long graphmodelId,WebSocketMessage message, ReceiverType receiverType)
    {
        graphModelRegistry.send(graphmodelId, message, receiverType);
    }

    public String getUserId(String ticket) {
    	PyroUserDB user = TicketRegistrationHandler.checkGetRelated(ticket);
    	String userId = ""+user.id; 
    	return userId;
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
