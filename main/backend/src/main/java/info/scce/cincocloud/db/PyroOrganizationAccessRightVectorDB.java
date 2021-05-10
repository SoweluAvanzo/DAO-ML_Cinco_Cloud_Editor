package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroOrganizationAccessRightVectorDB extends PanacheEntity {

    @javax.persistence.ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    public PyroUserDB user;

    @javax.persistence.ManyToOne
    public PyroOrganizationDB organization;

    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    @javax.persistence.ElementCollection
    public java.util.Collection<PyroOrganizationAccessRightDB> accessRights = new java.util.ArrayList<>();
}