package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroWorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class PyroWorkspaceImage extends RESTBaseImpl {

    public String name;
    public String imageName;
    public String imageVersion;
    public boolean published;
    public PyroUser user;
    public PyroProject project;

    public static PyroWorkspaceImage fromEntity(
            final PyroWorkspaceImageDB entity,
            final ObjectCache objectCache
    ) {
        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }

        final PyroWorkspaceImage result;
        result = new PyroWorkspaceImage();
        result.setId(entity.id);

        result.name = entity.name;
        result.imageName = entity.imageName;
        result.imageVersion = entity.imageVersion;
        result.published = entity.published;
        result.user = PyroUser.fromEntity(entity.user, objectCache);
        result.project = PyroProject.fromEntity(entity.project, objectCache);

        objectCache.putRestTo(entity, result);

        return result;
    }
}
