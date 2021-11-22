package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.List;

public class UserTO extends RESTBaseImpl {

  private List<ProjectTO> ownedProjects = new java.util.LinkedList<>();
  private List<UserSystemRole> systemRoles = new java.util.LinkedList<>();
  private String username;
  private String email;
  private FileReferenceTO profilePicture;

  public static UserTO fromEntity(final UserDB entity, final ObjectCache objectCache) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new UserTO();
    result.setId(entity.id);
    result.setemail(entity.email);
    result.setusername(entity.username);

    if (entity.profilePicture != null) {
      result.setprofilePicture(new FileReferenceTO(entity.profilePicture));
    }

    objectCache.putRestTo(entity, result);

    for (ProjectDB p : entity.ownedProjects) {
      result.getownedProjects().add(ProjectTO.fromEntity(p, objectCache));
    }

    for (UserSystemRole p : entity.systemRoles) {
      result.getsystemRoles().add(p);
    }

    return result;
  }

  @JsonProperty("ownedProjects")
  public List<ProjectTO> getownedProjects() {
    return this.ownedProjects;
  }

  @JsonProperty("ownedProjects")
  public void setownedProjects(final List<ProjectTO> ownedProjects) {
    this.ownedProjects = ownedProjects;
  }

  @JsonProperty("systemRoles")
  public List<UserSystemRole> getsystemRoles() {
    return this.systemRoles;
  }

  @JsonProperty("systemRoles")
  public void setsystemRoles(final List<UserSystemRole> systemRoles) {
    this.systemRoles = systemRoles;
  }

  @JsonProperty("username")
  public String getusername() {
    return this.username;
  }

  @JsonProperty("username")
  public void setusername(final String username) {
    this.username = username;
  }

  @JsonProperty("email")
  public String getemail() {
    return this.email;
  }

  @JsonProperty("email")
  public void setemail(final String email) {
    this.email = email;
  }

  @JsonProperty("profilePicture")
  public FileReferenceTO getprofilePicture() {
    return this.profilePicture;
  }

  @JsonProperty("profilePicture")
  public void setprofilePicture(final FileReferenceTO profilePicture) {
    this.profilePicture = profilePicture;
  }
}
