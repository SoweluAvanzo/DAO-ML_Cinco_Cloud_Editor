package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

  public static PanacheQuery<WorkspaceImageDB> findAllAccessibleImages(UserDB subject, Optional<String> search) {
    var parameters = Parameters.with("userId", subject.id);
    var searchQuery = "";

    if (search.isPresent()) {
      parameters = parameters.and("searchTerm", "%" + search.get().toLowerCase() + "%");
      searchQuery = " and "
              + "(LOWER(p.name) LIKE :searchTerm or "
              + "(p.owner is not null and LOWER(p.owner.name) LIKE :searchTerm) or "
              + "(org is not null and LOWER(org.name) LIKE :searchTerm))";
    }

    if (subject.isAdmin()) {
      return find("select distinct w from WorkspaceImageDB w "
                      + "join w.project p "
                      + "left join p.organization org "
                      + "where p.deletedAt = null and "
                      + "(w.published = true or "
                      + "p.owner.id = :userId) "
                      + searchQuery
                      + "order by w.id asc",
              parameters);
    } else {
      return find("select distinct w from WorkspaceImageDB w "
                      + "join w.project p "
                      + "left join p.organization org "
                      + "left join p.members projectMember "
                      + "left join org.members orgMember "
                      + "left join org.owners orgOwner "
                      + "where p.deletedAt = null and "
                      + "(w.published = true or "
                      + "p.owner.id = :userId or "
                      + "projectMember.id = :userId or "
                      + "orgMember.id = :userId or "
                      + "orgOwner.id = :userId) "
                      + searchQuery
                      + "order by w.id asc",
              parameters);
    }
  }

  public static PanacheQuery<WorkspaceImageDB> findAllFeaturedImages() {
    return find("published = true and featured = true");
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
