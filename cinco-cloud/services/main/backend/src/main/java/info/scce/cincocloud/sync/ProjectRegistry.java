package info.scce.cincocloud.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;

@Singleton
public class ProjectRegistry extends WebSocketRegistry {

  /**
   * Map: ProjectId -> Session[]
   */
  private final Map<Long, List<Session>> currentOpenSockets = new ConcurrentHashMap<>();

  @Inject
  public ProjectRegistry(ObjectMapper mapper) {
    super(mapper);
  }

  public void send(long projectId, WebSocketMessage message) {
    currentOpenSockets.getOrDefault(projectId, Collections.synchronizedList(new ArrayList<>()))
        .forEach(session -> send(session, message));
  }

  public void addSession(long projectId, Session session) {
    currentOpenSockets.putIfAbsent(projectId, Collections.synchronizedList(new ArrayList<>()));
    currentOpenSockets.get(projectId).add(session);
  }

  public void removeSession(long projectId, Session session) {
    if (currentOpenSockets.containsKey(projectId)) {
      currentOpenSockets.get(projectId).remove(session);
    }
  }

  public void closeSessions(long projectId) {
    ImmutableList.copyOf(currentOpenSockets.getOrDefault(projectId, new ArrayList<>()))
        .forEach(session -> {
          try {
            session.close();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            removeSession(projectId, session);
          }
        });
  }

  public List<Session> getSessions(long projectId) {
    final var sessions = currentOpenSockets.getOrDefault(projectId, new ArrayList<>());
    return Collections.unmodifiableList(sessions);
  }
}
