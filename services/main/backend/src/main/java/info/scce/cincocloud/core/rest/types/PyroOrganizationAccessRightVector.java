package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.PyroOrganizationAccessRightDB;
import info.scce.cincocloud.db.PyroOrganizationAccessRightVectorDB;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.LinkedList;
import java.util.List;

public class PyroOrganizationAccessRightVector extends RESTBaseImpl {

  private List<PyroOrganizationAccessRightDB> accessRights = new LinkedList<>();
  private PyroUser user;
  private PyroOrganization organization;

  public static PyroOrganizationAccessRightVector fromEntity(
      final PyroOrganizationAccessRightVectorDB entity,
      final info.scce.cincocloud.rest.ObjectCache objectCache) {

    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final PyroOrganizationAccessRightVector result;
    result = new PyroOrganizationAccessRightVector();
    result.setId(entity.id);

    result.setuser(PyroUser.fromEntity(entity.user, objectCache));
    result.setorganization(PyroOrganization.fromEntity(entity.organization, objectCache));

    objectCache.putRestTo(entity, result);

    for (PyroOrganizationAccessRightDB ar : entity.accessRights) {
      result.getaccessRights().add(ar);
    }

    return result;
  }

  @JsonProperty("accessRights")
  public List<PyroOrganizationAccessRightDB> getaccessRights() {
    return this.accessRights;
  }

  @JsonProperty("accessRights")
  public void setaccessRights(final List<PyroOrganizationAccessRightDB> accessRights) {
    this.accessRights = accessRights;
  }

  @JsonProperty("user")
  public PyroUser getuser() {
    return this.user;
  }

  @JsonProperty("user")
  public void setuser(final PyroUser user) {
    this.user = user;
  }

  @JsonProperty("organization")
  public PyroOrganization getorganization() {
    return this.organization;
  }

  @JsonProperty("organization")
  public void setorganization(final PyroOrganization organization) {
    this.organization = organization;
  }
}