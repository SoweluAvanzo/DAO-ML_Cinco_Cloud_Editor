package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class SettingsTO extends RESTBaseImpl {

  private boolean allowPublicUserRegistration;
  private boolean autoActivateUsers;

  private boolean sendMails;

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
    result.setautoActivateUsers(entity.autoActivateUsers);
    result.setsendMails(entity.sendMails);

    objectCache.putRestTo(entity, result);

    return result;
  }

  @JsonProperty("allowPublicUserRegistration")
  public boolean getallowPublicUserRegistration() {
    return allowPublicUserRegistration;
  }

  @JsonProperty("allowPublicUserRegistration")
  public void setallowPublicUserRegistration(final boolean allowPublicUserRegistration) {
    this.allowPublicUserRegistration = allowPublicUserRegistration;
  }

  @JsonProperty("autoActivateUsers")
  public boolean getautoActivateUsers() {
    return autoActivateUsers;
  }

  @JsonProperty("autoActivateUsers")
  public void setautoActivateUsers(final boolean autoActivateUsers) {
    this.autoActivateUsers = autoActivateUsers;
  }

  @JsonProperty("sendMails")
  public boolean getsendMails() {
    return sendMails;
  }

  @JsonProperty("sendMails")
  public void setsendMails(boolean sendMails) {
    this.sendMails = sendMails;
  }
}
