package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.OrganizationAccessRight;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.LinkedList;
import java.util.List;

public class OrganizationAccessRightVectorTO extends RESTBaseImpl {

  private List<OrganizationAccessRight> accessRights = new LinkedList<>();
  private UserTO user;
  private OrganizationTO organization;

  public static OrganizationAccessRightVectorTO fromEntity(
      final OrganizationAccessRightVectorDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new OrganizationAccessRightVectorTO();
    result.setId(entity.id);
    result.setuser(UserTO.fromEntity(entity.user, objectCache));
    result.setorganization(OrganizationTO.fromEntity(entity.organization, objectCache));

    objectCache.putRestTo(entity, result);

    for (OrganizationAccessRight ar : entity.accessRights) {
      result.getaccessRights().add(ar);
    }

    return result;
  }

  @JsonProperty("accessRights")
  public List<OrganizationAccessRight> getaccessRights() {
    return this.accessRights;
  }

  @JsonProperty("accessRights")
  public void setaccessRights(final List<OrganizationAccessRight> accessRights) {
    this.accessRights = accessRights;
  }

  @JsonProperty("user")
  public UserTO getuser() {
    return this.user;
  }

  @JsonProperty("user")
  public void setuser(final UserTO user) {
    this.user = user;
  }

  @JsonProperty("organization")
  public OrganizationTO getorganization() {
    return this.organization;
  }

  @JsonProperty("organization")
  public void setorganization(final OrganizationTO organization) {
    this.organization = organization;
  }
}
