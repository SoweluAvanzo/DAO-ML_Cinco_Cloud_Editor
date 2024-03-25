package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class BaseFileDB extends PanacheEntity {

  public String filename;
  public String fileExtension;
  public String contentType;

  @Transient
  public String getFullFilename() {
    return fileExtension == null
            ? filename
            : filename + "." + fileExtension;
  }
}

