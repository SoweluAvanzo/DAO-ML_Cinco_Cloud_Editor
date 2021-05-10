package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroOrganizationDB extends PanacheEntity {

    public String name;
    public String description;

    @javax.persistence.OneToOne(cascade = javax.persistence.CascadeType.ALL)
    public PyroStyleDB style;

    @javax.persistence.ManyToMany(cascade = javax.persistence.CascadeType.ALL)
    @javax.persistence.JoinTable(
            name = "PyroOrganizationDB_Owners",
            joinColumns = @javax.persistence.JoinColumn(name = "PyroOrganizationDB_id"),
            inverseJoinColumns = @javax.persistence.JoinColumn(name = "PyroUserDB_id")
    )
    public java.util.Collection<PyroUserDB> owners = new java.util.ArrayList<>();

    @javax.persistence.ManyToMany(cascade = javax.persistence.CascadeType.ALL)
    @javax.persistence.JoinTable(
            name = "PyroOrganizationDB_Members",
            joinColumns = @javax.persistence.JoinColumn(name = "PyroOrganizationDB_id"),
            inverseJoinColumns = @javax.persistence.JoinColumn(name = "PyroUserDB_id")
    )
    public java.util.Collection<PyroUserDB> members = new java.util.ArrayList<>();

    @javax.persistence.OneToMany
    public java.util.Collection<PyroProjectDB> projects = new java.util.ArrayList<>();
}