package info.scce.pyro.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import info.scce.pyro.rest.PyroSelectiveRestFilter;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;

/**
 * Author zweihoff
 */
public class WebSocketRegistry {

    public void send(Session session,WebSocketMessage message) {
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
            session.getAsyncRemote().sendText(response);
        } catch (Exception e) {
            //e.printStackTrace();
            try {
                session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close(Session session,int code) throws IOException {
        session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(code),""));
    }

}
