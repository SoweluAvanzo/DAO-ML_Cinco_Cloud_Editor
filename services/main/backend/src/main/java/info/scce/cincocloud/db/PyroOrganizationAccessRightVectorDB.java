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

@Entity()
public class PyroOrganizationAccessRightVectorDB extends PanacheEntity {

  @ManyToOne(cascade = CascadeType.ALL)
  public PyroUserDB user;

  @ManyToOne
  public PyroOrganizationDB organization;

  @Enumerated(EnumType.STRING)
  @ElementCollection
  public List<PyroOrganizationAccessRightDB> accessRights = new ArrayList<>();
}
