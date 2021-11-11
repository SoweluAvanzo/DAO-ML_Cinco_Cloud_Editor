package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroWorkspaceImageBuildJobDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.time.Instant;

public class PyroWorkspaceImageBuildJob extends RESTBaseImpl {

  public PyroProject project;
  public PyroWorkspaceImageBuildJobDB.Status status;
  public Instant startedAt;
  public Instant finishedAt;

  public static PyroWorkspaceImageBuildJob fromEntity(
      final PyroWorkspaceImageBuildJobDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new PyroWorkspaceImageBuildJob();
    result.setId(entity.id);
    result.project = PyroProject.fromEntity(entity.project, objectCache);
    result.startedAt = entity.startedAt;
    result.finishedAt = entity.finishedAt;
    result.status = entity.status;

    objectCache.putRestTo(entity, result);

    return result;
  }
}
