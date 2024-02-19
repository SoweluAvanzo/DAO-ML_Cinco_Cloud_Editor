package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.ArrayList;
import java.util.Collection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class UserDB extends PanacheEntity {

  public String name;
  @Column(columnDefinition = "citext")
  public String username;
  @Column(columnDefinition = "citext")
  public String email;
  public String password;
  public String activationKey;
  public boolean isActivated;
  public boolean isDeactivatedByAdmin;

  @OneToOne(cascade = CascadeType.ALL)
  public BaseFileDB profilePicture;

  @Enumerated(EnumType.STRING)
  @ElementCollection
  public Collection<UserSystemRole> systemRoles = new ArrayList<>();

  @OneToMany(mappedBy = "owner")
  public Collection<ProjectDB> personalProjects = new ArrayList<>();

  @ManyToMany(mappedBy = "owners")
  public Collection<OrganizationDB> ownedOrganizations = new ArrayList<>();

  @ManyToMany(mappedBy = "members")
  public Collection<OrganizationDB> memberedOrganizations = new ArrayList<>();

  @Transient
  public boolean isAdmin() {
    return systemRoles.contains(UserSystemRole.ADMIN);
  }
}
