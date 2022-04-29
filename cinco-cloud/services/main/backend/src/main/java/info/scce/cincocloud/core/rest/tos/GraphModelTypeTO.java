package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.GraphModelTypeDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class GraphModelTypeTO extends RESTBaseImpl {

  private String typeName;
  private String fileExtension;

  public static GraphModelTypeTO fromEntity(
      final GraphModelTypeDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var to = new GraphModelTypeTO();
    to.setId(entity.id);
    to.settypeName(entity.typeName);
    to.setfileExtension(entity.fileExtension);

    objectCache.putRestTo(entity, to);
    return to;
  }

  @JsonProperty("typeName")
  public String gettypeName() {
    return typeName;
  }

  @JsonProperty("typeName")
  public void settypeName(String typeName) {
    this.typeName = typeName;
  }

  @JsonProperty("fileExtension")
  public String getfileExtension() {
    return fileExtension;
  }

  @JsonProperty("fileExtension")
  public void setfileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }
}
