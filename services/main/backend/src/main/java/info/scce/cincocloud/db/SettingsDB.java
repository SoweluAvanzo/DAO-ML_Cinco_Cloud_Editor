package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity
public class SettingsDB extends PanacheEntity {

  public boolean globallyCreateOrganizations = true;

  public boolean allowPublicUserRegistration = true;
}
