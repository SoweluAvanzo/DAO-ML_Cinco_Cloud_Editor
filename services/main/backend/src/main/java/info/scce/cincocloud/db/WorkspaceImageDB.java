package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class WorkspaceImageDB extends PanacheEntity {

  @NotBlank
  public String imageVersion;

  @NotNull
  public boolean published = false;

  @NotNull
  public Instant createdAt = Instant.now();

  @NotNull
  public Instant updatedAt = Instant.now();

  @NotNull
  @OneToOne
  public ProjectDB project;

  @Column(columnDefinition = "uuid", updatable = false)
  public UUID uuid = UUID.randomUUID();

  public static Optional<WorkspaceImageDB> findByUUID(UUID uuid) {
    return find("uuid", uuid).firstResultOptional();
  }

  @Override
  public String toString() {
    return "WorkspaceImageDB{" +
        "id=" + id +
        ", imageVersion='" + imageVersion + '\'' +
        ", published=" + published +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", project=" + project +
        ", uuid=" + uuid +
        '}';
  }

  public String getImageName() {
    return uuid.toString() + ":" + imageVersion;
  }
}
