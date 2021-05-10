package entity.core;

import java.util.Random;
import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroUserDB extends PanacheEntity {
    
    public String username;
    public String email;
    public String password;
    public String activationKey;
    public boolean isActivated;
    
    @javax.persistence.OneToOne(cascade=javax.persistence.CascadeType.ALL)
    public entity.core.BaseFileDB profilePicture;
    
    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    @javax.persistence.ElementCollection
    public java.util.Collection<entity.core.PyroSystemRoleDB> systemRoles = new java.util.ArrayList<>();
    
    @javax.persistence.OneToMany(mappedBy="owner")
    public java.util.Collection<entity.core.PyroProjectDB> ownedProjects = new java.util.ArrayList<>();
    
    @javax.persistence.ManyToMany(mappedBy = "owners")
    public java.util.Collection<entity.core.PyroOrganizationDB> ownedOrganizations = new java.util.ArrayList<>();
    
    @javax.persistence.ManyToMany(mappedBy = "members")
    public java.util.Collection<entity.core.PyroOrganizationDB> memberedOrganizations = new java.util.ArrayList<>();

    public static PyroUserDB add(String email, String username, String password) { 
        return add(email, username, password, new java.util.LinkedList<>());
    }
    
    public static PyroUserDB add(String email, String username, String password, java.util.Collection<entity.core.PyroSystemRoleDB> roles) { 
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

