package info.scce.cincocloud.core;

import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.sync.ProjectRegistry;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
    project.delete();
  }

  public void checkPermission(ProjectDB project, SecurityContext securityContext) {
    final UserDB user = UserDB.getCurrentUser(securityContext);
    boolean isOwner = project.organization.owners.contains(user);
    boolean isMember = project.organization.members.contains(user);
    if (isOwner || isMember) {
      return;
    }
    throw new WebApplicationException(Response.Status.FORBIDDEN);
  }
}

