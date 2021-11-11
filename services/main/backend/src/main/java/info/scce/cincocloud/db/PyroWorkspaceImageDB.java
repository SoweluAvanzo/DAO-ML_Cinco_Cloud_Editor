package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity()
public class PyroWorkspaceImageDB extends PanacheEntity {

  @NotBlank
  public String name;

  @NotBlank
  public String imageName;

  @NotBlank
  public String imageVersion;

  @NotNull
  public boolean published = false;

  @NotNull
  public Instant createdAt = Instant.now();

  @NotNull
  public Instant updatedAt = Instant.now();

  @NotNull
  @ManyToOne
  public PyroUserDB user;

  @NotNull
  @OneToOne
  public PyroProjectDB project;

  public static Optional<PyroWorkspaceImageDB> findByImageName(String imageName) {
    return find("imageName", imageName).firstResultOptional();
  }

  @Override
  public String toString() {
    return "PyroWorkspaceImageDB{"
        + "name='" + name + '\''
        + ", imageName='" + imageName + '\''
        + ", imageVersion='" + imageVersion + '\''
        + ", published=" + published
        + ", createdAt=" + createdAt
        + ", updatedAt=" + updatedAt
        + ", project=" + project
        + ", user=" + user
        + '}';
  }
}
