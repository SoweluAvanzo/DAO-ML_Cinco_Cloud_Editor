package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.PyroSettingsDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class PyroSettings extends RESTBaseImpl {

  private PyroStyle style;
  private boolean globallyCreateOrganizations;

  public static PyroSettings fromEntity(
      final PyroSettingsDB entity,
      final ObjectCache objectCache) {

    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final PyroSettings result;
    result = new PyroSettings();
    result.setId(entity.id);
    result.setstyle(PyroStyle.fromEntity(entity.style, objectCache));
    result.setgloballyCreateOrganizations(entity.globallyCreateOrganizations);

    objectCache.putRestTo(entity, result);

    return result;
  }

  @JsonProperty("style")
  public PyroStyle getstyle() {
    return this.style;
  }

  @JsonProperty("style")
  public void setstyle(final PyroStyle style) {
    this.style = style;
  }

  @JsonProperty("globallyCreateOrganizations")
  public boolean getgloballyCreateOrganizations() {
    return this.globallyCreateOrganizations;
  }

  @JsonProperty("globallyCreateOrganizations")
  public void setgloballyCreateOrganizations(final boolean globallyCreateOrganizations) {
    this.globallyCreateOrganizations = globallyCreateOrganizations;
  }
}