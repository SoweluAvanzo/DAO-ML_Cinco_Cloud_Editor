package info.scce.cincocloud.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import info.scce.cincocloud.rest.PyroSelectiveRestFilter;

public class WebSocketRegistry {

    public void send(Session session, WebSocketMessage message) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter("PYRO_Selective_Filter", new PyroSelectiveRestFilter()));
        String response = null;
        try {
            response = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            try {
                session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            session.getBasicRemote().sendText(response);
        } catch (IOException e) {
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
