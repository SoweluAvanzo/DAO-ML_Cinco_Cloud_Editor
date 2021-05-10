package info.scce.pyro.core;

import info.scce.pyro.sync.ProjectWebSocket;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.SecurityContext;

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
		
		deletePermissionVectors(pp);
		deleteEditorGrids(pp);
		
		// remove project from organization
		pp.organization.projects.remove(pp);
		pp.delete();
	}

	private void deletePermissionVectors(entity.core.PyroProjectDB project) {
		final java.util.List<entity.core.PyroGraphModelPermissionVectorDB> result = entity.core.PyroGraphModelPermissionVectorDB.list("project", project);
		java.util.Iterator<entity.core.PyroGraphModelPermissionVectorDB> iter = result.iterator();
		while(iter.hasNext()) {
			entity.core.PyroGraphModelPermissionVectorDB vector = iter.next();
			result.remove(vector);
			vector.delete();
			iter = result.iterator();
		}
	}
	
	private void deleteEditorGrids(entity.core.PyroProjectDB project) {
		final java.util.List<entity.core.PyroEditorGridDB> result = entity.core.PyroEditorGridDB.list("project", project);
		java.util.Iterator<entity.core.PyroEditorGridDB> iter = result.iterator();
		while(iter.hasNext()) {
			entity.core.PyroEditorGridDB grid = iter.next();
			result.remove(grid);
			grid.delete();
			iter = result.iterator();
		}
	}

    /**
     * Create default graphmodel permission vectors for all users of the organization.
     * Should be called when a new project is created.
     * 
     * @param project
     */
    public void createDefaultGraphModelPermissionVectors(entity.core.PyroProjectDB project) {
		final List<entity.core.PyroUserDB> users = new ArrayList<>();
		users.addAll(project.organization.owners);
		users.addAll(project.organization.members);
		
		for (final entity.core.PyroUserDB user: users) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.EMPTY;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			if (user.equals(project.owner)) {
				vector.permissions.add(entity.core.PyroCrudOperationDB.CREATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.UPDATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.DELETE);
			} else {
			}
			vector.persist();
		}
		for (final entity.core.PyroUserDB user: users) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.PRIME_REFS;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			if (user.equals(project.owner)) {
				vector.permissions.add(entity.core.PyroCrudOperationDB.CREATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.UPDATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.DELETE);
			} else {
			}
			vector.persist();
		}
		for (final entity.core.PyroUserDB user: users) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.HIERARCHY;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			if (user.equals(project.owner)) {
				vector.permissions.add(entity.core.PyroCrudOperationDB.CREATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.UPDATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.DELETE);
			} else {
			}
			vector.persist();
		}
		for (final entity.core.PyroUserDB user: users) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.HOOKS_AND_ACTIONS;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			if (user.equals(project.owner)) {
				vector.permissions.add(entity.core.PyroCrudOperationDB.CREATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.UPDATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.DELETE);
			} else {
			}
			vector.persist();
		}
		for (final entity.core.PyroUserDB user: users) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.FLOW_GRAPH;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			if (user.equals(project.owner)) {
				vector.permissions.add(entity.core.PyroCrudOperationDB.CREATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.UPDATE);
				vector.permissions.add(entity.core.PyroCrudOperationDB.DELETE);
			} else {
			}
			vector.persist();
		}
	}
    
    /**
     * Create default graphmodel permission vectors for when a new user is added to an organization
     * 
     * @param user
     * @param organization
     */
    public void createDefaultGraphModelPermissionVectors(
    		entity.core.PyroUserDB user,
    		entity.core.PyroOrganizationDB organization) {
    			
		for (final entity.core.PyroProjectDB project: organization.projects) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.EMPTY;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			vector.persist();
		}
		for (final entity.core.PyroProjectDB project: organization.projects) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.PRIME_REFS;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			vector.persist();
		}
		for (final entity.core.PyroProjectDB project: organization.projects) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.HIERARCHY;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			vector.persist();
		}
		for (final entity.core.PyroProjectDB project: organization.projects) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.HOOKS_AND_ACTIONS;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			vector.persist();
		}
		for (final entity.core.PyroProjectDB project: organization.projects) {
			final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
			vector.user = user;
			vector.project = project;
			vector.graphModelType = entity.core.PyroGraphModelTypeDB.FLOW_GRAPH;
			vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
			vector.persist();
		}
	}
}

