package de.jabc.cinco.meta.plugin.pyro.backend.core

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import java.util.List
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRegistry

class EditorLayoutService extends Generatable {
	List<EditorViewPlugin> eps

	new(GeneratorCompound gc) {
		super(gc)
		eps = new EditorViewPluginRegistry().getPlugins(gc);
	}

	def fileNameDispatcher() '''EditorLayoutService.java'''

	def contentDispatcher() '''
	package info.scce.pyro.core;
		
	
	import java.util.*;
	
	import javax.enterprise.context.ApplicationScoped;
	
	@ApplicationScoped
	public class EditorLayoutService {
		
		public entity.core.PyroEditorGridItemDB createGridArea(
			final entity.core.PyroEditorGridDB grid, 
			Long x, 
			Long y, 
			Long width, 
			Long height
		) {
			final entity.core.PyroEditorGridItemDB item = new entity.core.PyroEditorGridItemDB();
			item.persist();
			item.x = x;
			item.y = y;
			item.width = width;
			item.height = height;
			grid.items.add(item);
			return item;
		}
		
		public entity.core.PyroEditorWidgetDB createWidget(
			final entity.core.PyroEditorGridDB grid,
			final entity.core.PyroEditorGridItemDB area,
			final String tab,
			final String key
		) {
			final entity.core.PyroEditorWidgetDB widget = new entity.core.PyroEditorWidgetDB();
			widget.persist();
			if(area != null) {
				area.widgets.add(widget);
				widget.area = area;
			}
			widget.tab = tab;
			widget.key = key;
			widget.position = 0L;
			widget.grid = grid;
			return widget;
		}
		
		private void link(
				final entity.core.PyroEditorGridDB grid,
				final entity.core.PyroEditorGridItemDB area,
				final entity.core.PyroEditorWidgetDB widget) {
			if(widget == null) {
				return;
			}
			widget.area = area;
			area.widgets.add(widget);
			grid.availableWidgets.remove(widget);
		}
		
		public void setLayout(final entity.core.PyroEditorGridDB grid, EditorGridLayout layout) {		
			// remove all existing areas, make widgets invisible
			for (entity.core.PyroEditorGridItemDB area: grid.items) {
				for (entity.core.PyroEditorWidgetDB w: area.widgets) {
					w.area = null;
					grid.availableWidgets.add(w);
					w.persist();
				}
				area.widgets.clear();
				area.delete();
			}
			grid.items.clear();
			
			// key -> widget
			final Map<String, entity.core.PyroEditorWidgetDB> widgetMap = new HashMap<>();
			for (entity.core.PyroEditorWidgetDB w: grid.availableWidgets) {
				widgetMap.put(w.key, w);
			}
			
			// place widgets
			switch(layout) {
				case DEFAULT:
					link(grid, createGridArea(grid, 0L, 3L, 3L, 3L), widgetMap.get("map"));
					link(grid, createGridArea(grid, 3L, 0L, 6L, 6L), widgetMap.get("canvas"));
					link(grid, createGridArea(grid, 3L, 6L, 6L, 3L), widgetMap.get("properties"));
					link(grid, createGridArea(grid, 9L, 0L, 3L, 3L), widgetMap.get("palette"));
					link(grid, createGridArea(grid, 9L, 3L, 3L, 3L), widgetMap.get("checks"));
					break;
				case MINIMAL:
					link(grid, createGridArea(grid, 3L, 0L, 6L, 6L), widgetMap.get("canvas"));
					link(grid, createGridArea(grid, 9L, 0L, 3L, 3L), widgetMap.get("palette"));
					link(grid, createGridArea(grid, 9L, 3L, 3L, 3L), widgetMap.get("properties"));
					break;
				case MAXIMUM_CANVAS:
					link(grid, createGridArea(grid, 0L, 3L, 3L, 3L), widgetMap.get("palette"));
					link(grid, createGridArea(grid, 0L, 6L, 3L, 3L), widgetMap.get("properties"));
					link(grid, createGridArea(grid, 3L, 0L, 9L, 9L), widgetMap.get("canvas"));
					break;
				case COMPLETE:
					link(grid, createGridArea(grid, 3L, 0L, 6L, 6L), widgetMap.get("canvas"));
					link(grid, createGridArea(grid, 3L, 6L, 6L, 3L), widgetMap.get("properties"));
					link(grid, createGridArea(grid, 9L, 0L, 3L, 6L), widgetMap.get("palette"));
					link(grid, createGridArea(grid, 0L, 0L, 3L, 3L), widgetMap.get("map"));
					link(grid, createGridArea(grid, 9L, 6L, 3L, 3L), widgetMap.get("checks"));
					link(grid, createGridArea(grid, 0L, 3L, 3L, 3L), widgetMap.get("command_history"));
					«linkPlugins»
					break;
				default:
					break;
			}
		}
	}
	'''
	
	def linkPlugins() {
		val plugins = eps.filter[pluginComponent.fetchURL!==null].map[pluginComponent].indexed
		'''
			«IF !plugins.empty»
				
				// plugins
				«FOR pc:plugins»
					link(grid, createGridArea(grid, «(pc.key % 4) * 3»L, «9 + Math.floorDiv(pc.key, 4) * 3»L, 3L, 3L), widgetMap.get("«pc.value.key»"));
				«ENDFOR»
			«ENDIF»
		'''
	}
}
