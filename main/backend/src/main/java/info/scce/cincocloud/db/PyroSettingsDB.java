package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroSettingsDB extends PanacheEntity {

    @javax.persistence.OneToOne(cascade = javax.persistence.CascadeType.ALL)
    public PyroStyleDB style;
    public boolean globallyCreateOrganizations;
}