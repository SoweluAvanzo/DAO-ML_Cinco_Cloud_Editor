package info.scce.cincocloud.core;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.sync.ProjectRegistry;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

@ApplicationScoped
@Transactional
public class ProjectService {

  @Inject
  ProjectRegistry projectRegistry;

  public void deleteById(final long id) {
    final ProjectDB project = ProjectDB.findById(id);

    if (project.owner != null) {
      project.owner.personalProjects.remove(project);
      // Set to null to prevent cascading deletion of the owner
      project.owner = null;
    }

    projectRegistry.closeSessions(project.id);

    // remove project from organization
    if (project.organization != null) {
      project.organization.projects.remove(project);
      project.organization.persist();

      // null references and mark project as deleted
      project.organization = null;
    }
    project.deletedAt = Instant.now();

    final var buildJobIds = project.buildJobs.stream()
        .map(j -> j.id)
        .collect(Collectors.toList());

    WorkspaceImageBuildJobDB.deleteByIdIn(buildJobIds);

    project.buildJobs.clear();
    project.persist();
  }

  public void checkPermission(ProjectDB project, UserDB subject) {
    List<Collection<UserDB>> authorizedUserLists = new LinkedList<>();
    if (project.owner != null) {
      authorizedUserLists.add(List.of(project.owner));
    }
    authorizedUserLists.add(project.members);
    if (project.organization != null) {
      authorizedUserLists.add(project.organization.owners);
      authorizedUserLists.add(project.organization.members);
    }
    for (var authorizedUserList : authorizedUserLists) {
      if (authorizedUserList.contains(subject)) {
        return;
      }
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

  public boolean userOwnsProject(UserDB user, ProjectDB project) {
    return project.matchOnOwnership(
        owner -> user.equals(owner),
        organization -> organization.owners.contains(user)
    );
  }
}

