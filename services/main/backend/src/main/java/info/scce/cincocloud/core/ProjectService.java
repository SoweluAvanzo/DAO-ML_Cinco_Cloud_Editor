package info.scce.cincocloud.core;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.sync.ProjectRegistry;
import java.time.Instant;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@ApplicationScoped
@Transactional
public class ProjectService {

  @Inject
  ProjectRegistry projectRegistry;

  public void deleteById(UserDB user, final long id, SecurityContext securityContext) {
    final ProjectDB project = ProjectDB.findById(id);
    checkPermission(project, securityContext);

    if (project.owner.equals(user)) {
      project.owner.ownedProjects.remove(project);
      project.owner = null;
    }

    projectRegistry.closeSessions(project.id);

    // remove project from organization
    project.organization.projects.remove(project);
    project.organization.persist();

    // null references and mark project as deleted
    project.organization = null;
    project.deletedAt = Instant.now();

    final var buildJobIds = project.buildJobs.stream()
        .map(j -> j.id)
        .collect(Collectors.toList());

    WorkspaceImageBuildJobDB.deleteByIdIn(buildJobIds);

    project.buildJobs.clear();
    project.persist();
  }

  public void checkPermission(ProjectDB project, SecurityContext securityContext) {
    final UserDB user = UserDB.getCurrentUser(securityContext);
    boolean isOwner = project.organization.owners.contains(user);
    boolean isMember = project.organization.members.contains(user);
    if (isOwner || isMember) {
      return;
    }
    throw new RestException(Status.FORBIDDEN, "user can not access the project");
  }

  /**
   * Check if the project exists in the database and if the project is not deleted.
   *
   * @param project The project that is checked.
   */
  public void checkIfProjectExists(ProjectDB project) {
    if (project == null || project.deletedAt != null) {
      throw new RestException(Status.NOT_FOUND, "project can not be found");
    }
  }
}

