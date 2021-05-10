package info.scce.pyro.core;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.SecurityContext;
import info.scce.pyro.sync.ProjectWebSocket;

@ApplicationScoped
public class ProjectService {

    @javax.inject.Inject
    ProjectWebSocket projectWebSocket;

    @javax.inject.Inject
    GraphModelController graphModelController;
		
	public void deleteById(entity.core.PyroUserDB user, final long id,SecurityContext securityContext) {
		final entity.core.PyroProjectDB pp = entity.core.PyroProjectDB.findById(id);
		
		graphModelController.checkPermission(pp,securityContext);
		
		if(pp.owner.equals(user)){
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
}

