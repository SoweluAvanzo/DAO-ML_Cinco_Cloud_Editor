package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.ProjectType;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectTO extends RESTBaseImpl {

  private UserTO owner;
  private OrganizationTO organization;
  private WorkspaceImageTO image;
  private WorkspaceImageTO template;
  private ProjectType type;
  private String name;
  private String description;
  private List<GraphModelTypeTO> graphModelTypes = new ArrayList<>();

  public static ProjectTO fromEntity(final ProjectDB entity, final ObjectCache objectCache) {

    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new ProjectTO();
    result.setId(entity.id);
    result.setType(entity.type);
    result.setname(entity.name);
    result.setdescription(entity.description);

    objectCache.putRestTo(entity, result);

    if (entity.image != null) {
      result.setimage(WorkspaceImageTO.fromEntity(entity.image, objectCache));
    }

    if (entity.template != null) {
      result.setTemplate(WorkspaceImageTO.fromEntity(entity.template, objectCache));
    }

    if (entity.organization != null) {
      result.setorganization(OrganizationTO.fromEntity(entity.organization, objectCache));
    }

    if (entity.owner != null) {
      result.setowner(UserTO.fromEntity(entity.owner, objectCache));
    }

    if (entity.graphModelTypes != null && !entity.graphModelTypes.isEmpty()) {
      result.setgraphModelTypes(entity.graphModelTypes.stream()
          .map(g -> GraphModelTypeTO.fromEntity(g, objectCache))
          .collect(Collectors.toList()));
    }

    return result;
  }

  @JsonProperty("owner")
  public UserTO getowner() {
    return this.owner;
  }

  @JsonProperty("owner")
  public void setowner(final UserTO owner) {
    this.owner = owner;
  }

  @JsonProperty("organization")
  public OrganizationTO getorganization() {
    return this.organization;
  }

  @JsonProperty("organization")
  public void setorganization(final OrganizationTO organization) {
    this.organization = organization;
  }

  @JsonProperty("image")
  public WorkspaceImageTO getimage() {
    return this.image;
  }

  @JsonProperty("image")
  public void setimage(final WorkspaceImageTO image) {
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
  public WorkspaceImageTO getTemplate() {
    return template;
  }

  @JsonProperty("template")
  public void setTemplate(WorkspaceImageTO template) {
    this.template = template;
  }

  @JsonProperty("type")
  public ProjectType getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(ProjectType type) {
    this.type = type;
  }

  @JsonProperty("graphModelTypes")
  public List<GraphModelTypeTO> getgraphModelTypes() {
    return graphModelTypes;
  }

  @JsonProperty("graphModelTypes")
  public void setgraphModelTypes(List<GraphModelTypeTO> graphModelTypes) {
    this.graphModelTypes = graphModelTypes;
  }
}
