package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class OrganizationDB extends PanacheEntity {

  public String name;
  public String description;

  @OneToOne(cascade = CascadeType.ALL)
  public BaseFileDB logo;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "OrganizationDB_owners",
      joinColumns = @JoinColumn(name = "OrganizationDB_id"),
      inverseJoinColumns = @JoinColumn(name = "UserDB_id")
  )
  public Collection<UserDB> owners = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "OrganizationDB_members",
      joinColumns = @JoinColumn(name = "OrganizationDB_id"),
      inverseJoinColumns = @JoinColumn(name = "UserDB_id")
  )
  public Collection<UserDB> members = new ArrayList<>();

  @OneToMany
  public Collection<ProjectDB> projects = new ArrayList<>();
}
