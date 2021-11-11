package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.LinkedList;
import java.util.List;

public class PyroOrganization extends RESTBaseImpl {

  private String name;
  private String description;
  private List<PyroUser> owners = new LinkedList<>();
  private List<PyroUser> members = new LinkedList<>();
  private List<PyroProject> projects = new LinkedList<>();
  private PyroStyle style;

  public static PyroOrganization fromEntity(
      final PyroOrganizationDB entity,
      final ObjectCache objectCache
  ) {

    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final PyroOrganization result;
    result = new PyroOrganization();
    result.setId(entity.id);

    result.setname(entity.name);
    result.setdescription(entity.description);
    result.setstyle(PyroStyle.fromEntity(entity.style, objectCache));
    objectCache.putRestTo(entity, result);

    for (PyroUserDB o : entity.owners) {
      result.getowners().add(PyroUser.fromEntity(o, objectCache));
    }

    for (PyroUserDB m : entity.members) {
      result.getmembers().add(PyroUser.fromEntity(m, objectCache));
    }

    for (PyroProjectDB p : entity.projects) {
      result.getprojects().add(PyroProject.fromEntity(p, objectCache));
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
  public List<PyroUser> getowners() {
    return this.owners;
  }

  @JsonProperty("owners")
  public void setowners(final List<PyroUser> owners) {
    this.owners = owners;
  }

  @JsonProperty("members")
  public List<PyroUser> getmembers() {
    return this.members;
  }

  @JsonProperty("members")
  public void setmembers(final List<PyroUser> members) {
    this.members = members;
  }

  @JsonProperty("projects")
  public List<PyroProject> getprojects() {
    return this.projects;
  }

  @JsonProperty("projects")
  public void setprojects(final List<PyroProject> projects) {
    this.projects = projects;
  }

  @JsonProperty("style")
  public PyroStyle getstyle() {
    return this.style;
  }

  @JsonProperty("style")
  public void setstyle(final PyroStyle style) {
    this.style = style;
  }
}