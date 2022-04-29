package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.time.Instant;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(
    name = "WorkspaceImageBuildJobDB.findByProjectId",
    query = "select job from WorkspaceImageBuildJobDB job inner join job.project p where p.id = ?1"
)
@NamedQuery(
    name = "WorkspaceImageBuildJobDB.findByProjectIdOrderByStartedAtDesc",
    query = ""
        + "select job "
        + "from WorkspaceImageBuildJobDB job "
        + "inner join job.project p "
        + "where p.id = ?1 "
        + "order by job.startedAt desc"
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

  public static Long deleteByIdIn(List<Long> ids) {
    return delete("id in ?1", ids);
  }

  public static PanacheQuery<WorkspaceImageBuildJobDB> findByProjectId(Long projectId) {
    return find("#WorkspaceImageBuildJobDB.findByProjectId", projectId);
  }

  public static PanacheQuery<WorkspaceImageBuildJobDB> findByProjectIdOrderByStartedAtDesc(Long projectId) {
    return find("#WorkspaceImageBuildJobDB.findByProjectIdOrderByStartedAtDesc", projectId);
  }

  public enum Status {
    PENDING,
    BUILDING,
    FINISHED_WITH_SUCCESS,
    FINISHED_WITH_FAILURE,
    ABORTED
  }

  @Transient
  public boolean isTerminated() {
    return status.equals(Status.ABORTED)
        || status.equals(Status.FINISHED_WITH_FAILURE)
        || status.equals(Status.FINISHED_WITH_SUCCESS);
  }
}
