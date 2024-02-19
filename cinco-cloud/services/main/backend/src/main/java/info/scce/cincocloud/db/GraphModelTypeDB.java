package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

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
