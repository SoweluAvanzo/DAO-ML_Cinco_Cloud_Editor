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

@Entity()
public class PyroOrganizationDB extends PanacheEntity {

  public String name;
  public String description;

  @OneToOne(cascade = CascadeType.ALL)
  public PyroStyleDB style;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "PyroOrganizationDB_Owners",
      joinColumns = @JoinColumn(name = "PyroOrganizationDB_id"),
      inverseJoinColumns = @JoinColumn(name = "PyroUserDB_id")
  )
  public Collection<PyroUserDB> owners = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "PyroOrganizationDB_Members",
      joinColumns = @JoinColumn(name = "PyroOrganizationDB_id"),
      inverseJoinColumns = @JoinColumn(name = "PyroUserDB_id")
  )
  public Collection<PyroUserDB> members = new ArrayList<>();

  @OneToMany
  public Collection<PyroProjectDB> projects = new ArrayList<>();
}