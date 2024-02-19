package info.scce.cincocloud.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Logger;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
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
      LOGGER.log(Level.INFO, "Failed to serialize message.", e);
      try {
        close(session, CloseReason.CloseCodes.CLOSED_ABNORMALLY, "Internal error.");
      } catch (IOException e1) {
        LOGGER.log(Level.INFO, "Failed to close session.", e1);
      }
    }
  }

  public void close(Session session, CloseReason.CloseCodes code, String message) throws IOException {
    session.close(new CloseReason(code, message));
  }
}
