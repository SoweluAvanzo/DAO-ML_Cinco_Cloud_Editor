package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class StopProjectPodsTaskDB extends PanacheEntity {

  @NotNull
  private Long projectId;

  @NotNull
  private Instant createdAt;

  public StopProjectPodsTaskDB() {
    this.createdAt = Instant.now();
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
