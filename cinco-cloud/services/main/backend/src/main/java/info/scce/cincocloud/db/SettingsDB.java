package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class SettingsDB extends PanacheEntity {

  public boolean allowPublicUserRegistration = true;

  public boolean autoActivateUsers = false;

  public boolean sendMails = true;

  /**
   * If true, for each user that registers a list of featured projects is created.
   */
  public boolean createDefaultProjects = false;

  public String archetypeImage = "";
}
