package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

@Entity
public class OrganizationAccessRightVectorDB extends PanacheEntity {

  @ManyToOne(cascade = CascadeType.ALL)
  public UserDB user;

  @ManyToOne
  public OrganizationDB organization;

  @Enumerated(EnumType.STRING)
  @ElementCollection
  public Set<OrganizationAccessRight> accessRights = new HashSet<>();

  public static PanacheQuery<OrganizationAccessRightVectorDB> findOrganizationAccessRightsForUser(UserDB user, OrganizationDB org) {
    return find("user = ?1 and organization = ?2", user, org);
  }

  public static PanacheQuery<OrganizationAccessRightVectorDB> findAccessRightVectors(OrganizationDB org) {
    return find("organization = ?1", org);
  }
}
