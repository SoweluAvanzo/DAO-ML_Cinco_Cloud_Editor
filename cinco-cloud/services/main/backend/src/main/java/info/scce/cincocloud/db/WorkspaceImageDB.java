package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import java.util.List;
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
  public boolean featured = false;

  @NotNull
  @OneToOne
  public ProjectDB project;

  @Column(columnDefinition = "uuid", updatable = false)
  public UUID uuid = UUID.randomUUID();

  public static Optional<WorkspaceImageDB> findByUUID(UUID uuid) {
    return find("uuid", uuid).firstResultOptional();
  }

  public static List<WorkspaceImageDB> findAllWhereProjectIsNotDeleted() {
    return find("project.deletedAt = null and published = true order by id asc").list();
  }

  public static List<WorkspaceImageDB> findAllFeatured() {
    return find("published = true and featured = true").list();
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
        ", featured=" + featured +
        ", uuid=" + uuid +
        '}';
  }
}
