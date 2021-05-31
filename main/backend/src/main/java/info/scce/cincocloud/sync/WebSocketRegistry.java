package info.scce.cincocloud.sync;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import info.scce.cincocloud.rest.PyroSelectiveRestFilter;

public abstract class WebSocketRegistry {

    final ObjectMapper mapper;

    public WebSocketRegistry() {
        mapper = new ObjectMapper();
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter("PYRO_Selective_Filter", new PyroSelectiveRestFilter()));
    }

    public void send(Session session, WebSocketMessage message) {
        try {
            final var res = mapper.writeValueAsString(message);
            session.getAsyncRemote().sendText(res, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
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
