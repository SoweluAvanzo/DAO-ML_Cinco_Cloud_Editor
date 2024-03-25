package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.SettingsDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class SettingsTO extends RESTBaseImpl {

  private boolean allowPublicUserRegistration;
  private boolean autoActivateUsers;
  private boolean sendMails;
  private boolean createDefaultProjects;
  private String archetypeImage;

  public static SettingsTO fromEntity(
      final SettingsDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var to = new SettingsTO();
    to.setId(entity.id);
    to.setallowPublicUserRegistration(entity.allowPublicUserRegistration);
    to.setautoActivateUsers(entity.autoActivateUsers);
    to.setsendMails(entity.sendMails);
    to.setArchetypeImage(entity.archetypeImage);
    to.setCreateDefaultProjects(entity.createDefaultProjects);
    objectCache.putRestTo(entity, to);
    return to;
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

  @JsonProperty("archetypeImage")
  public String getArchetypeImage() {
    return archetypeImage;
  }

  @JsonProperty("archetypeImage")
  public void setArchetypeImage(String archetypeImage) {
    this.archetypeImage = archetypeImage;
  }

  @JsonProperty("createDefaultProjects")
  public boolean isCreateDefaultProjects() {
    return createDefaultProjects;
  }

  @JsonProperty("createDefaultProjects")
  public void setCreateDefaultProjects(boolean createDefaultProjects) {
    this.createDefaultProjects = createDefaultProjects;
  }
}
