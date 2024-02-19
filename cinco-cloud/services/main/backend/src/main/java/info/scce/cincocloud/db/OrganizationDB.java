package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.ArrayList;
import java.util.Collection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
@NamedQuery(
    name = "OrganizationDB.findWhereUserIsOwnerOrMember",
    query = "select distinct organization "
        + "from OrganizationDB organization, UserDB user "
        + "where (user.id = ?1 and user in elements(organization.owners)) "
        + "or (user.id = ?1 and user in elements(organization.members))"
)
public class OrganizationDB extends PanacheEntity {

  @Column(columnDefinition = "citext")
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

  public static PanacheQuery<OrganizationDB> findOrganizationsWhereUserIsOwnerOrMember(long userId) {
    return find("#OrganizationDB.findWhereUserIsOwnerOrMember", userId);
  }
}
