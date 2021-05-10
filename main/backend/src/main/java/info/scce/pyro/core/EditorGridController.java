package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
	
@javax.transaction.Transactional
@javax.enterprise.context.RequestScoped
@javax.ws.rs.Path("/project/{projectId}/editorGrid")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class EditorGridController {
	
	
	@javax.inject.Inject
	EditorLayoutService editorLayoutService;
	
	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("/")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response get(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("projectId") final long projectId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			checkPermission(subject, project);
			
			final java.util.List<entity.core.PyroEditorGridDB> result = entity.core.PyroEditorGridDB.list("user = ?1 and project = ?2",subject,project);
			return javax.ws.rs.core.Response.ok(PyroEditorGrid.fromEntity(result.get(0), objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.PUT
	@javax.ws.rs.Path("/{gridId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response update(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId,
			@javax.ws.rs.PathParam("gridId") final long gridId,
			final PyroEditorGrid grid) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			final entity.core.PyroEditorGridDB gridInDB = entity.core.PyroEditorGridDB.findById(gridId);
			checkPermission(subject, project, gridInDB);
			
			final java.util.Map<Long, entity.core.PyroEditorGridItemDB> itemMap = 
					gridInDB.items.stream()
						.collect(java.util.stream.Collectors.toMap((i) -> i.id, i -> i));
			
			for (final PyroEditorGridItem item: grid.getitems()) {
				entity.core.PyroEditorGridItemDB itemInDB = itemMap.get(item.getId());
				itemInDB.x = item.getx();
				itemInDB.y = item.gety();
				itemInDB.width = item.getwidth();
				itemInDB.height = item.getheight();
				itemInDB.persist();
			}
			
			return javax.ws.rs.core.Response.ok(PyroEditorGrid.fromEntity(gridInDB, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{gridId}/setLayout/{layout}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response setLayout(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId,
			@javax.ws.rs.PathParam("gridId") final long gridId,
			@javax.ws.rs.PathParam("layout") final EditorGridLayout layout) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			final entity.core.PyroEditorGridDB gridInDB = entity.core.PyroEditorGridDB.findById(gridId);
			checkPermission(subject, project, gridInDB);
			
			editorLayoutService.setLayout(gridInDB, layout);
						
			return javax.ws.rs.core.Response.ok(PyroEditorGrid.fromEntity(gridInDB, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{gridId}/areas")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response createWidgetArea(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId,
			@javax.ws.rs.PathParam("gridId") final long gridId) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			final entity.core.PyroEditorGridDB gridInDB = entity.core.PyroEditorGridDB.findById(gridId);
			checkPermission(subject, project, gridInDB);
			
			final entity.core.PyroEditorGridItemDB newArea = new entity.core.PyroEditorGridItemDB();
			
			Long y = 0L;
			for (entity.core.PyroEditorGridItemDB a: gridInDB.items) {
				y =  java.lang.Math.max(y, a.y + a.height);
			}
			
			newArea.y = y;
			newArea.x = 0L;
			newArea.width = 3L;
			newArea.height = 3L;
			
			gridInDB.items.add(newArea);
			
			newArea.persist();
			gridInDB.persist();
			
			return javax.ws.rs.core.Response.ok(PyroEditorGridItem.fromEntity(newArea, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{gridId}/areas/{areaId}/remove")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response removeWidgetArea(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId,
			@javax.ws.rs.PathParam("gridId") final long gridId,
			@javax.ws.rs.PathParam("areaId") final long areaId) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			final entity.core.PyroEditorGridDB gridInDB = entity.core.PyroEditorGridDB.findById(gridId);
			checkPermission(subject, project, gridInDB);
			
			final entity.core.PyroEditorGridItemDB areaInDB = entity.core.PyroEditorGridItemDB.findById(areaId);
			if (!gridInDB.items.contains(areaInDB)) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			// hide all widgets inside the area
			for (entity.core.PyroEditorWidgetDB w: areaInDB.widgets) {
				w.area = null;
				gridInDB.availableWidgets.add(w);
				w.persist();
				
			}
			areaInDB.widgets.clear();
			gridInDB.items.remove(areaInDB);
			
			gridInDB.persist();
			areaInDB.delete();
			
			return javax.ws.rs.core.Response.ok(PyroEditorGrid.fromEntity(gridInDB, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{gridId}/widgets/{widgetId}/remove")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response removeWidget(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId,
			@javax.ws.rs.PathParam("gridId") final long gridId,
			@javax.ws.rs.PathParam("widgetId") final long widgetId) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			final entity.core.PyroEditorGridDB gridInDB = entity.core.PyroEditorGridDB.findById(gridId);
			checkPermission(subject, project, gridInDB);
			
			final entity.core.PyroEditorWidgetDB widgetInDB = entity.core.PyroEditorWidgetDB.findById(widgetId);
			if (!widgetInDB.grid.equals(gridInDB)) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			// move widget to grid
			final entity.core.PyroEditorGridItemDB area = widgetInDB.area;
			area.widgets.remove(widgetInDB);
			area.persist();
			widgetInDB.area = null;
			widgetInDB.position = 0L;
			widgetInDB.persist();
			gridInDB.availableWidgets.add(widgetInDB);
			
			
			// remove widget area if it does not contain any widgets
			if (area.widgets.isEmpty()) {
				gridInDB.items.remove(area);
				area.delete();
			}
			gridInDB.persist();
			
			return javax.ws.rs.core.Response.ok(PyroEditorGrid.fromEntity(gridInDB, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/{gridId}/widgets/{widgetId}/moveTo/{areaId}")
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response moveWidget(
			@javax.ws.rs.core.Context SecurityContext securityContext,
			@javax.ws.rs.PathParam("projectId") final long projectId,
			@javax.ws.rs.PathParam("gridId") final long gridId,
			@javax.ws.rs.PathParam("widgetId") final long widgetId,
			@javax.ws.rs.PathParam("areaId") final long areaId) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		if (subject != null) {
			final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(projectId);
			final entity.core.PyroEditorGridDB gridInDB = entity.core.PyroEditorGridDB.findById(gridId);
			checkPermission(subject, project, gridInDB);
			
			final entity.core.PyroEditorWidgetDB widgetInDB = entity.core.PyroEditorWidgetDB.findById(widgetId);
			if (!widgetInDB.grid.equals(gridInDB)) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			final entity.core.PyroEditorGridItemDB areaInDB = entity.core.PyroEditorGridItemDB.findById(areaId);
			if (!gridInDB.items.contains(areaInDB)) return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
			
			// move to another area
			if (widgetInDB.area == null) {
				gridInDB.availableWidgets.remove(widgetInDB);
			} else {
				widgetInDB.area.widgets.remove(widgetInDB);
				if (widgetInDB.area.widgets.isEmpty()) {
					gridInDB.items.remove(widgetInDB.area);
					widgetInDB.area.delete();
				}
			}
			widgetInDB.area = areaInDB;
			widgetInDB.position = Long.valueOf(areaInDB.widgets.size());
			widgetInDB.persist();
			gridInDB.persist();
			areaInDB.widgets.add(widgetInDB);
			areaInDB.persist();
			
			return javax.ws.rs.core.Response.ok(PyroEditorGrid.fromEntity(gridInDB, objectCache)).build();
		}
		
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	}
	
	
	public void checkPermission(
			entity.core.PyroUserDB user,
			entity.core.PyroProjectDB project) {
		
		if (user == null) {
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		} else if (project == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else if (!(project.organization.members.contains(user) || project.organization.owners.contains(user))) {
			throw new WebApplicationException(Response.Status.FORBIDDEN);	
		}
	}
	
	public void checkPermission(
			entity.core.PyroUserDB user,
			entity.core.PyroProjectDB project,
			entity.core.PyroEditorGridDB grid) {
		
		checkPermission(user, project);
		
		if (grid == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else if (!grid.project.equals(project)) {
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
	}
}	
