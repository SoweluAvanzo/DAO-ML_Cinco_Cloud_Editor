package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.Random;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity()
public class PyroUserDB extends PanacheEntity {

    public String username;
    public String email;
    public String password;
    public String activationKey;
    public boolean isActivated;

    @OneToOne(cascade = javax.persistence.CascadeType.ALL)
    public BaseFileDB profilePicture;

    @Enumerated(javax.persistence.EnumType.STRING)
    @ElementCollection
    public java.util.Collection<PyroSystemRoleDB> systemRoles = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "owner")
    public java.util.Collection<PyroProjectDB> ownedProjects = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user")
    public java.util.Collection<PyroWorkspaceImageDB> images = new java.util.ArrayList<>();

    @ManyToMany(mappedBy = "owners")
    public java.util.Collection<PyroOrganizationDB> ownedOrganizations = new java.util.ArrayList<>();

    @ManyToMany(mappedBy = "members")
    public java.util.Collection<PyroOrganizationDB> memberedOrganizations = new java.util.ArrayList<>();

    public static PyroUserDB add(String email, String username, String password) {
        return add(email, username, password, new java.util.LinkedList<>());
    }

    public static PyroUserDB add(String email, String username, String password, java.util.Collection<PyroSystemRoleDB> roles) {
        PyroUserDB user = new PyroUserDB();
        user.email = email;
        user.username = username;
        user.password = password;
        Random random = new Random();
        String generatedString = random.ints(97, 122 + 1)
                .limit(15)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        user.activationKey = generatedString;
        user.systemRoles = roles;
        user.isActivated = false;
        user.persist();
        return user;
    }

    public static PyroUserDB getCurrentUser(javax.ws.rs.core.SecurityContext context) {
        return PyroUserDB.find("email", context.getUserPrincipal().getName()).firstResult();
    }
}

