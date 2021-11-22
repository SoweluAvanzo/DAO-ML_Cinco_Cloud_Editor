package info.scce.cincocloud.core.rest.tos;

import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.time.Instant;

public class WorkspaceImageBuildJobTO extends RESTBaseImpl {

  public ProjectTO project;
  public WorkspaceImageBuildJobDB.Status status;
  public Instant startedAt;
  public Instant finishedAt;

  public static WorkspaceImageBuildJobTO fromEntity(
      final WorkspaceImageBuildJobDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new WorkspaceImageBuildJobTO();
    result.setId(entity.id);
    result.project = ProjectTO.fromEntity(entity.project, objectCache);
    result.startedAt = entity.startedAt;
    result.finishedAt = entity.finishedAt;
    result.status = entity.status;

    objectCache.putRestTo(entity, result);

    return result;
  }
}
