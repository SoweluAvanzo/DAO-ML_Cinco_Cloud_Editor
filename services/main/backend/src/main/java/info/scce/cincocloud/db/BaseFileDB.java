package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity
public class BaseFileDB extends PanacheEntity {

  public String filename;
  public String fileExtension;
  public String path;
  public String contentType;
}

