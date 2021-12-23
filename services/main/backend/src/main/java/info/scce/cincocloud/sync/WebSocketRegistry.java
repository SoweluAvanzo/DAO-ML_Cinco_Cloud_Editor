package info.scce.cincocloud.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import org.jboss.logmanager.Level;

public abstract class WebSocketRegistry {

  private static final Logger LOGGER = Logger.getLogger(WebSocketRegistry.class.getName());

  protected final ObjectMapper mapper;

  public WebSocketRegistry(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public void send(Session session, WebSocketMessage message) {
    try {
      final var res = mapper.writeValueAsString(message);
      session.getAsyncRemote().sendText(res, result -> {
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
