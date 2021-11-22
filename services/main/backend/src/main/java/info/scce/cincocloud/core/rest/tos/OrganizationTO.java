package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.LinkedList;
import java.util.List;

public class OrganizationTO extends RESTBaseImpl {

  private String name;
  private String description;
  private List<UserTO> owners = new LinkedList<>();
  private List<UserTO> members = new LinkedList<>();
  private List<ProjectTO> projects = new LinkedList<>();
  private StyleTO style;

  public static OrganizationTO fromEntity(
      final OrganizationDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new OrganizationTO();
    result.setId(entity.id);
    result.setname(entity.name);
    result.setdescription(entity.description);
    result.setstyle(StyleTO.fromEntity(entity.style, objectCache));

    objectCache.putRestTo(entity, result);

    for (UserDB o : entity.owners) {
      result.getowners().add(UserTO.fromEntity(o, objectCache));
    }

    for (UserDB m : entity.members) {
      result.getmembers().add(UserTO.fromEntity(m, objectCache));
    }

    for (ProjectDB p : entity.projects) {
      result.getprojects().add(ProjectTO.fromEntity(p, objectCache));
    }

    return result;
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

  @JsonProperty("owners")
  public List<UserTO> getowners() {
    return this.owners;
  }

  @JsonProperty("owners")
  public void setowners(final List<UserTO> owners) {
    this.owners = owners;
  }

  @JsonProperty("members")
  public List<UserTO> getmembers() {
    return this.members;
  }

  @JsonProperty("members")
  public void setmembers(final List<UserTO> members) {
    this.members = members;
  }

  @JsonProperty("projects")
  public List<ProjectTO> getprojects() {
    return this.projects;
  }

  @JsonProperty("projects")
  public void setprojects(final List<ProjectTO> projects) {
    this.projects = projects;
  }

  @JsonProperty("style")
  public StyleTO getstyle() {
    return this.style;
  }

  @JsonProperty("style")
  public void setstyle(final StyleTO style) {
    this.style = style;
  }
}
