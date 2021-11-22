package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class SettingsDB extends PanacheEntity {

  @OneToOne(cascade = CascadeType.ALL)
  public StyleDB style;

  public boolean globallyCreateOrganizations;
}
