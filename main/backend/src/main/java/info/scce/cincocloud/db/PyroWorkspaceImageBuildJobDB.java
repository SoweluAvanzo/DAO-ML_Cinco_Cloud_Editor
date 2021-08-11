package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class PyroWorkspaceImageBuildJobDB extends PanacheEntity {

    public enum Status {
        PENDING,
        BUILDING,
        FINISHED_WITH_SUCCESS,
        FINISHED_WITH_FAILURE,
        ABORTED
    }

    @NotNull
    @OneToOne
    public PyroProjectDB project;

    @Enumerated(EnumType.STRING)
    public Status status = Status.PENDING;

    public PyroWorkspaceImageBuildJobDB() {
    }

    public PyroWorkspaceImageBuildJobDB(@NotNull PyroProjectDB project, Status status) {
        this.project = project;
        this.status = status;
    }

    public static List<PyroWorkspaceImageBuildJobDB> findByStatus(Status status) {
        return find("status", status).list();
    }

    public static List<PyroWorkspaceImageBuildJobDB> findByProjectId(Long projectId) {
        return findAll().stream()
                .map(e -> (PyroWorkspaceImageBuildJobDB) e)
                .filter(e -> e.project.id.equals(projectId))
                .collect(Collectors.toList());
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
