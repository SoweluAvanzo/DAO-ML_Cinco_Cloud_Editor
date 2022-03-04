package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class GraphModelTypeDB extends PanacheEntity {

  @NotNull
  @ManyToOne
  @JoinColumn(name = "project_id")
  public ProjectDB project;

  @NotNull
  public String typeName;

  @NotNull
  public String fileExtension;
}
