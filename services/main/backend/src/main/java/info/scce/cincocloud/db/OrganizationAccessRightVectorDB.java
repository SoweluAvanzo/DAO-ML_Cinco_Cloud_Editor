package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Entity
public class OrganizationAccessRightVectorDB extends PanacheEntity {

  @ManyToOne(cascade = CascadeType.ALL)
  public UserDB user;

  @ManyToOne
  public OrganizationDB organization;

  @Enumerated(EnumType.STRING)
  @ElementCollection
  public List<OrganizationAccessRight> accessRights = new ArrayList<>();
}
