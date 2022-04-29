package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class SettingsTO extends RESTBaseImpl {

  private boolean globallyCreateOrganizations;
  private boolean allowPublicUserRegistration;

  public static SettingsTO fromEntity(
      final SettingsDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new SettingsTO();
    result.setId(entity.id);
    result.setallowPublicUserRegistration(entity.allowPublicUserRegistration);

    objectCache.putRestTo(entity, result);

    return result;
  }

  @JsonProperty("globallyCreateOrganizations")
  public boolean getgloballyCreateOrganizations() {
    return this.globallyCreateOrganizations;
  }

  @JsonProperty("globallyCreateOrganizations")
  public void setgloballyCreateOrganizations(final boolean globallyCreateOrganizations) {
    this.globallyCreateOrganizations = globallyCreateOrganizations;
  }

  @JsonProperty("allowPublicUserRegistration")
  public boolean getallowPublicUserRegistration() {
    return allowPublicUserRegistration;
  }

  @JsonProperty("allowPublicUserRegistration")
  public void setallowPublicUserRegistration(final boolean allowPublicUserRegistration) {
    this.allowPublicUserRegistration = allowPublicUserRegistration;
  }
}
