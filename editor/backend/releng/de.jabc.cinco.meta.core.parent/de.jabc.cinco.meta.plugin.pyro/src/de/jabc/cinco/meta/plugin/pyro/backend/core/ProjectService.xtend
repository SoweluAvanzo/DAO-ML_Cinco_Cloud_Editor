package de.jabc.cinco.meta.plugin.pyro.backend.core

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRegistry
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import java.util.List

class ProjectService extends Generatable {
	List<EditorViewPlugin> eps

	new(GeneratorCompound gc) {
		super(gc)
		
		eps = new EditorViewPluginRegistry().getPlugins(gc);
	}

	def fileNameDispatcher() '''ProjectService.java'''

	def contentDispatcher() '''	
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
		    EditorLayoutService editorLayoutService;
		    
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
					// deleteEditorWidgets(grid);
					result.remove(grid);
					grid.delete();
					iter = result.iterator();
				}
			}
			
			public void createDefaultEditorGrid(
				final entity.core.PyroUserDB user,
				final entity.core.PyroOrganizationDB org) {
				
				for (final entity.core.PyroProjectDB project: org.projects) {
					createDefaultEditorGrid(user, project);
				}
			}
			
			public void createDefaultEditorGrid(final entity.core.PyroProjectDB project) {
				final List<entity.core.PyroUserDB> users = new ArrayList<>();
				users.addAll(project.organization.owners);
				users.addAll(project.organization.members);
				
				for (final entity.core.PyroUserDB user: users) {
					createDefaultEditorGrid(user, project);
				}
			}
						
			private void createDefaultEditorGrid(
				final entity.core.PyroUserDB user,
				final entity.core.PyroProjectDB project) {
				
				final entity.core.PyroEditorGridDB grid = new entity.core.PyroEditorGridDB();
				grid.persist();
				grid.user = user;
				grid.project = project;
				grid.persist();
				
				«IF !gc.editorLayout.empty»
					«FOR e:gc.editorLayout»
						{
							«e» editorLayout = new «e»();
							editorLayout.init(editorLayoutService);
							editorLayout.execute(grid);
						}
					«ENDFOR»
				«ELSE»
					// explorer
					final entity.core.PyroEditorGridItemDB explorerItem = editorLayoutService.createGridArea(grid, 0L, 0L, 3L, 3L);				
					final entity.core.PyroEditorWidgetDB explorerWidget = editorLayoutService.createWidget(grid, explorerItem, "Explorer", "explorer");
					
					// map
					final entity.core.PyroEditorGridItemDB mapItem = editorLayoutService.createGridArea(grid, 0L, 3L, 3L, 3L);				
					final entity.core.PyroEditorWidgetDB mapWidget = editorLayoutService.createWidget(grid, mapItem, "Map", "map");
					
					// canvas
					final entity.core.PyroEditorGridItemDB canvasItem = editorLayoutService.createGridArea(grid, 3L, 0L, 6L, 6L);				
					final entity.core.PyroEditorWidgetDB canvasWidget = editorLayoutService.createWidget(grid, canvasItem, "Canvas", "canvas");
					
					// properties
					final entity.core.PyroEditorGridItemDB propsItem = editorLayoutService.createGridArea(grid, 3L, 6L, 6L, 3L);				
					final entity.core.PyroEditorWidgetDB propsWidget = editorLayoutService.createWidget(grid, propsItem, "Properties", "properties");
					
					// palette
					final entity.core.PyroEditorGridItemDB paletteItem = editorLayoutService.createGridArea(grid, 9L, 0L, 3L, 3L);				
					final entity.core.PyroEditorWidgetDB paletteWidget = editorLayoutService.createWidget(grid, paletteItem, "Palette", "palette");
					
					// checks
					final entity.core.PyroEditorGridItemDB checksItem = editorLayoutService.createGridArea(grid, 9L, 3L, 3L, 3L);				
					final entity.core.PyroEditorWidgetDB checksWidget = editorLayoutService.createWidget(grid, checksItem, "Checks", "checks");
					
					// command history, not visible by default
					final entity.core.PyroEditorWidgetDB cmdHistoryWidget = editorLayoutService.createWidget(grid, null, "Command History", "command_history");
					grid.availableWidgets.add(cmdHistoryWidget);
					
					// add widgets for registered plugins
					// plugins aren't assigned a position and are not visible by default
					«FOR pc:eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent]»
						final entity.core.PyroEditorWidgetDB plugin_«pc.key»Widget = editorLayoutService.createWidget(grid, null, "«pc.tab»", "«pc.key»");
						grid.availableWidgets.add(plugin_«pc.key»Widget);
					«ENDFOR»
					grid.persist();
				«ENDIF»
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
				
				«FOR g: gc.graphMopdels»
					for (final entity.core.PyroUserDB user: users) {
						final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
						vector.user = user;
						vector.project = project;
						vector.graphModelType = entity.core.PyroGraphModelTypeDB.«g.name.toUnderScoreCase»;
						vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
						if (user.equals(project.owner)) {
							vector.permissions.add(entity.core.PyroCrudOperationDB.CREATE);
							vector.permissions.add(entity.core.PyroCrudOperationDB.UPDATE);
							vector.permissions.add(entity.core.PyroCrudOperationDB.DELETE);
						} else {
							«FOR right:g.defaultRights»
								vector.permissions.add(entity.core.PyroCrudOperationDB.«right.toUpperCase»);
							«ENDFOR»
						}
						vector.persist();
					}
				«ENDFOR»
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
		    			
				«FOR g: gc.graphMopdels»
					for (final entity.core.PyroProjectDB project: organization.projects) {
						final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
						vector.user = user;
						vector.project = project;
						vector.graphModelType = entity.core.PyroGraphModelTypeDB.«g.name.toUnderScoreCase»;
						vector.permissions.add(entity.core.PyroCrudOperationDB.READ);
						«FOR right:g.defaultRights»
							vector.permissions.add(entity.core.PyroCrudOperationDB.«right.toUpperCase»);
						«ENDFOR»
						vector.persist();
					}
				«ENDFOR»
			}
		}
		
	'''
}
