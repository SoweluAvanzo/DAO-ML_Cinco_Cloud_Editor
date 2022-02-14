package info.scce.cincocloud.core.rest.tos;

import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.UUID;

public class WorkspaceImageTO extends RESTBaseImpl {

  public String imageVersion;
  public boolean published;
  public ProjectTO project;
  public UUID uuid;

  public static WorkspaceImageTO fromEntity(
      final WorkspaceImageDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new WorkspaceImageTO();
    result.setId(entity.id);
    result.imageVersion = entity.imageVersion;
    result.published = entity.published;
    result.project = ProjectTO.fromEntity(entity.project, objectCache);
    result.uuid = entity.uuid;

    objectCache.putRestTo(entity, result);

    return result;
  }
}
