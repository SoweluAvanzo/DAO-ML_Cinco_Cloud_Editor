package info.scce.cincocloud.sync;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import org.jboss.logmanager.Level;
import info.scce.cincocloud.rest.CincoCloudSelectiveRestFilter;

public abstract class WebSocketRegistry {

    private static final Logger LOGGER = Logger.getLogger(WebSocketRegistry.class.getName());

    final ObjectMapper mapper;

    public WebSocketRegistry() {
        mapper = new ObjectMapper();
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter("CincoCloud_Selective_Filter", new CincoCloudSelectiveRestFilter()));
    }

    public void send(Session session, WebSocketMessage message) {
        try {
            final var res = mapper.writeValueAsString(message);
            session.getAsyncRemote().sendText(res, result ->  {
                if (result.getException() != null) {
                    LOGGER.log(Level.DEBUG, "Unable to send message: " + result.getException());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            try {
                session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close(Session session, int code) throws IOException {
        session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), ""));
    }

}
