package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity()
public class PyroSettingsDB extends PanacheEntity {

    @OneToOne(cascade = javax.persistence.CascadeType.ALL)
    public PyroStyleDB style;

    public boolean globallyCreateOrganizations;
}