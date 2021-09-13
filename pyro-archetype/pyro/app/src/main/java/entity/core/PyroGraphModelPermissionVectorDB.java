package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroGraphModelPermissionVectorDB extends PanacheEntity {
    
    public long userId;

    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    public entity.core.PyroGraphModelTypeDB graphModelType;

    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    @javax.persistence.ElementCollection
    public java.util.Collection<entity.core.PyroCrudOperationDB> permissions = new java.util.ArrayList<>();
}