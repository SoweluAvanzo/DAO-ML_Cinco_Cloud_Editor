package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class UserDB extends PanacheEntity {

  public String username;
  public String email;
  public String password;
  public String activationKey;
  public boolean isActivated;

  @OneToOne(cascade = javax.persistence.CascadeType.ALL)
  public BaseFileDB profilePicture;

  @Enumerated(javax.persistence.EnumType.STRING)
  @ElementCollection
  public Collection<UserSystemRole> systemRoles = new ArrayList<>();

  @OneToMany(mappedBy = "owner")
  public Collection<ProjectDB> ownedProjects = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  public Collection<WorkspaceImageDB> images = new ArrayList<>();

  @ManyToMany(mappedBy = "owners")
  public Collection<OrganizationDB> ownedOrganizations = new ArrayList<>();

  @ManyToMany(mappedBy = "members")
  public Collection<OrganizationDB> memberedOrganizations = new ArrayList<>();

  public static UserDB add(String email, String username, String password) {
    return add(email, username, password, new LinkedList<>());
  }

  public static UserDB add(String email, String username, String password,
      Collection<UserSystemRole> roles) {
    UserDB user = new UserDB();
    user.email = email;
    user.username = username;
    user.password = password;
    Random random = new Random();
    user.activationKey = random.ints(97, 122 + 1)
        .limit(15)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    user.systemRoles = roles;
    user.isActivated = false;
    user.persist();
    return user;
  }

  public static UserDB getCurrentUser(javax.ws.rs.core.SecurityContext context) {
    return UserDB.find("email", context.getUserPrincipal().getName()).firstResult();
  }
}

