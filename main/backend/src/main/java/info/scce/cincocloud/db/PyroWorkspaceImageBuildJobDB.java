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
        name = "PyroWorkspaceImageBuildJobDB.findByProjectId",
        query = "select job from PyroWorkspaceImageBuildJobDB job inner join job.project p where p.id = ?1"
)
public class PyroWorkspaceImageBuildJobDB extends PanacheEntity {

    public enum Status {
        PENDING,
        BUILDING,
        FINISHED_WITH_SUCCESS,
        FINISHED_WITH_FAILURE,
        ABORTED
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_PyroUserDB_id")
    public PyroProjectDB project;


    @Enumerated(EnumType.STRING)
    public Status status = Status.PENDING;

    public Instant startedAt;

    public Instant finishedAt;

    public PyroWorkspaceImageBuildJobDB() {
    }

    public PyroWorkspaceImageBuildJobDB(@NotNull PyroProjectDB project, Status status) {
        this.project = project;
        this.status = status;
    }

    public static PanacheQuery<PyroWorkspaceImageBuildJobDB> findByProjectId(Long projectId) {
        return find("#PyroWorkspaceImageBuildJobDB.findByProjectId", projectId);
    }

    public static PanacheQuery<PyroWorkspaceImageBuildJobDB> findByProjectId(Long projectId, Sort sort) {
        return find("#PyroWorkspaceImageBuildJobDB.findByProjectId", sort, projectId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PyroWorkspaceImageBuildJobDB)) {
            return false;
        }
        PyroWorkspaceImageBuildJobDB that = (PyroWorkspaceImageBuildJobDB) o;
        return Objects.equals(project, that.project) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, status);
    }
}
