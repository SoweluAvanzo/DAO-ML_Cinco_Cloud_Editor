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
		
		
		import java.util.*;
		import java.util.stream.Collectors;
		import java.util.stream.Stream;
		
		import javax.enterprise.context.ApplicationScoped;
		import javax.ws.rs.core.SecurityContext;
		
		@ApplicationScoped
		public class ProjectService {
		    
		    @javax.inject.Inject
		    EditorLayoutService editorLayoutService;
		    
		    @javax.inject.Inject
		    GraphModelController graphModelController;
				
			public entity.core.PyroEditorGridDB createDefaultEditorGrid(
				final long userId) {
				
				final entity.core.PyroEditorGridDB grid = new entity.core.PyroEditorGridDB();
				grid.persist();
				grid.userId = userId;
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

				return grid;
			}
					    
		    
		    /**
		     * Create default graphmodel permission vectors for when a new user is added to an organization
		     * 
		     * @param user
		     * @param organization
		     */
		    public void createDefaultGraphModelPermissionVectors(
		    		entity.core.PyroUserDB user) {
		    			
				«FOR g: gc.graphMopdels»
					{
						final entity.core.PyroGraphModelPermissionVectorDB vector = new entity.core.PyroGraphModelPermissionVectorDB();
						vector.userId = user.id;
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
