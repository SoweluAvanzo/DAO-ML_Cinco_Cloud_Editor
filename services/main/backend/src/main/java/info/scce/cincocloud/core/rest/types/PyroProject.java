package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroProjectTypeDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class PyroProject extends RESTBaseImpl {

  private PyroUser owner;
  private PyroOrganization organization;
  private PyroWorkspaceImage image;
  private PyroWorkspaceImage template;
  private PyroProjectTypeDB type;
  private String name;
  private String description;

  public static PyroProject fromEntity(final PyroProjectDB entity, final ObjectCache objectCache) {

    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }
    final PyroProject result;
    result = new PyroProject();
    result.setId(entity.id);
    result.setType(entity.type);
    result.setname(entity.name);
    result.setdescription(entity.description);

    objectCache.putRestTo(entity, result);

    if (entity.image != null) {
      result.setimage(PyroWorkspaceImage.fromEntity(entity.image, objectCache));
    }

    if (entity.template != null) {
      result.setTemplate(PyroWorkspaceImage.fromEntity(entity.template, objectCache));
    }

    if (entity.organization != null) {
      result.setorganization(PyroOrganization.fromEntity(entity.organization, objectCache));
    }

    if (entity.owner != null) {
      result.setowner(PyroUser.fromEntity(entity.owner, objectCache));
    }
    return result;
  }

  @JsonProperty("owner")
  public PyroUser getowner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setowner(final PyroUser owner) {
    this.owner = owner;
  }

  @JsonProperty("organization")
  public PyroOrganization getorganization() {
    return this.organization;
  }

  @JsonProperty("organization")
  public void setorganization(final PyroOrganization organization) {
    this.organization = organization;
  }

  @JsonProperty("image")
  public PyroWorkspaceImage getimage() {
    return this.image;
  }

  @JsonProperty("image")
  public void setimage(final PyroWorkspaceImage image) {
    this.image = image;
  }

  @JsonProperty("name")
  public String getname() {
    return this.name;
  }

  @JsonProperty("name")
  public void setname(final String name) {
    this.name = name;
  }

  @JsonProperty("description")
  public String getdescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setdescription(final String description) {
    this.description = description;
  }

  @JsonProperty("template")
  public PyroWorkspaceImage getTemplate() {
    return template;
  }

  @JsonProperty("template")
  public void setTemplate(PyroWorkspaceImage template) {
    this.template = template;
  }

  @JsonProperty("type")
  public PyroProjectTypeDB getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(PyroProjectTypeDB type) {
    this.type = type;
  }
}