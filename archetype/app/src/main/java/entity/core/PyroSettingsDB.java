package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroSettingsDB extends PanacheEntity {
    
    @javax.persistence.OneToOne(cascade=javax.persistence.CascadeType.ALL)
    public entity.core.PyroStyleDB style;
    public boolean globallyCreateOrganizations;
}