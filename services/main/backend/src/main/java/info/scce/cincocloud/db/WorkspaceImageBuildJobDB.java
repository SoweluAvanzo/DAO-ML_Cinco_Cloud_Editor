package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(
    name = "WorkspaceImageBuildJobDB.findByProjectId",
    query = "select job from WorkspaceImageBuildJobDB job inner join job.project p where p.id = ?1"
)
public class WorkspaceImageBuildJobDB extends PanacheEntity {

  @NotNull
  @ManyToOne
  @JoinColumn(name = "project_UserDB_id")
  public ProjectDB project;

  @Enumerated(EnumType.STRING)
  public Status status = Status.PENDING;

  public Instant startedAt;

  public Instant finishedAt;

  public WorkspaceImageBuildJobDB() {
  }

  public WorkspaceImageBuildJobDB(@NotNull ProjectDB project, Status status) {
    this.project = project;
    this.status = status;
  }

  public static PanacheQuery<WorkspaceImageBuildJobDB> findByProjectId(Long projectId) {
    return find("#WorkspaceImageBuildJobDB.findByProjectId", projectId);
  }

  public static PanacheQuery<WorkspaceImageBuildJobDB> findByProjectId(Long projectId,
      Sort sort) {
    return find("#WorkspaceImageBuildJobDB.findByProjectId", sort, projectId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WorkspaceImageBuildJobDB)) {
      return false;
    }
    WorkspaceImageBuildJobDB that = (WorkspaceImageBuildJobDB) o;
    return Objects.equals(project, that.project)
        && status == that.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(project, status);
  }

  public enum Status {
    PENDING,
    BUILDING,
    FINISHED_WITH_SUCCESS,
    FINISHED_WITH_FAILURE,
    ABORTED
  }
}
