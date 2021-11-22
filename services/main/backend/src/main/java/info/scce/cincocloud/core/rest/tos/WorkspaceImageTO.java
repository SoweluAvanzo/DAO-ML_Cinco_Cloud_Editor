package info.scce.cincocloud.core.rest.tos;

import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class WorkspaceImageTO extends RESTBaseImpl {

  public String name;
  public String imageName;
  public String imageVersion;
  public boolean published;
  public UserTO user;
  public ProjectTO project;

  public static WorkspaceImageTO fromEntity(
      final WorkspaceImageDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new WorkspaceImageTO();
    result.setId(entity.id);
    result.name = entity.name;
    result.imageName = entity.imageName;
    result.imageVersion = entity.imageVersion;
    result.published = entity.published;
    result.user = UserTO.fromEntity(entity.user, objectCache);
    result.project = ProjectTO.fromEntity(entity.project, objectCache);

    objectCache.putRestTo(entity, result);

    return result;
  }
}
