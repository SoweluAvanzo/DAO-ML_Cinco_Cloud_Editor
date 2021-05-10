package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroOrganizationAccessRightVectorDB extends PanacheEntity {
    
    @javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
    public entity.core.PyroUserDB user;
    
    @javax.persistence.ManyToOne
    public entity.core.PyroOrganizationDB organization;
    
    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    @javax.persistence.ElementCollection
    public java.util.Collection<entity.core.PyroOrganizationAccessRightDB> accessRights = new java.util.ArrayList<>();
}