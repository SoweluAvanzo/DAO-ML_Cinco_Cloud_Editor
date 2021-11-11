package info.scce.cincocloud.core;

import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.sync.ProjectWebSocket;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  ProjectWebSocket projectWebSocket;

  public void deleteById(PyroUserDB user, final long id, SecurityContext securityContext) {
    final PyroProjectDB pp = PyroProjectDB.findById(id);
    checkPermission(pp, securityContext);

    if (pp.owner.equals(user)) {
      pp.owner.ownedProjects.remove(pp);
      pp.owner = null;
      projectWebSocket.updateUserList(pp.id, Collections.emptyList());
    } else {
      projectWebSocket.updateUserList(pp.id, Stream.of(pp.owner.id).collect(Collectors.toList()));
    }

    // remove project from organization
    pp.organization.projects.remove(pp);
    pp.delete();
  }

  public void checkPermission(PyroProjectDB project, SecurityContext securityContext) {
    final PyroUserDB user = PyroUserDB.getCurrentUser(securityContext);
    boolean isOwner = project.organization.owners.contains(user);
    boolean isMember = project.organization.members.contains(user);
    if (isOwner || isMember) {
      return;
    }
    throw new WebApplicationException(Response.Status.FORBIDDEN);
  }
}

