package info.scce.pyro.core;

import info.scce.pyro.externallibrary.rest.ExternalLibraryList;
import info.scce.pyro.externallibrary.rest.ExternalLibrary;
import entity.core.PyroFolderDB;
import entity.core.PyroProjectDB;
import entity.core.PyroFileContainerDB;
import info.scce.pyro.core.rest.types.CreateEcore;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.sync.ProjectWebSocket;
import info.scce.pyro.sync.WebSocketMessage;
import javax.ws.rs.core.SecurityContext;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.ws.rs.WebApplicationException;

import info.scce.pyro.plugin.rest.TreeViewRest;

import javax.ws.rs.core.Response;
import java.io.IOException;

@javax.transaction.Transactional
@javax.ws.rs.Path("/externallibrary")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class ExternalLibraryController {

	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.inject.Inject
	ProjectWebSocket projectWebSocket;

	@javax.inject.Inject
	GraphModelWebSocket graphModelWebSocket;
	
	@javax.inject.Inject
	GraphModelController graphModelController;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("read/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
		final java.util.Set<entity.externallibrary.ExternalLibraryDB> list = new java.util.HashSet<>();
				
		final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(id);
		if(project != null) { // if it's a project
			list.addAll(
				collectProjectFiles(project)
			);
		} else {
			final entity.core.PyroFolderDB folder = entity.core.PyroFolderDB.findById(id);
			if(folder != null) { // if it's a folder
				list.addAll(
					collectProjectFiles(folder)
				);
			} else {
				throw new WebApplicationException("The specified id does neither relates to a Project nor a Folder!");
			}
		}
		
		return Response.ok(ExternalLibraryList.fromEntity(list, objectCache))
							.build();
	}
	
	public java.util.Set<entity.externallibrary.ExternalLibraryDB> collectProjectFiles(PanacheEntity fileContainer)
	{
		java.util.Set<entity.externallibrary.ExternalLibraryDB> found = new java.util.HashSet<>();
		
		if(fileContainer instanceof entity.core.PyroFolderDB) {
			entity.core.PyroFolderDB folder = (entity.core.PyroFolderDB) fileContainer;
			found.addAll(
				folder.files_ExternalLibrary
					.stream()
					.collect(java.util.stream.Collectors.toSet())
			);
			folder.innerFolders.forEach(f->found.addAll(collectProjectFiles(f)));
		} else if(fileContainer instanceof entity.core.PyroProjectDB) {
			entity.core.PyroProjectDB project = (entity.core.PyroProjectDB) fileContainer;
			found.addAll(
				project.files_ExternalLibrary
					.stream()
					.collect(java.util.stream.Collectors.toSet())
			);
			project.innerFolders.forEach(f->found.addAll(collectProjectFiles(f)));
		}
		
		return found;
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createEcore(@javax.ws.rs.core.Context SecurityContext securityContext, CreateEcore ecore) {

		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final PyroFileContainerDB container = PyroFileContainerDB.findById(ecore.getparentId());
		if(container==null||subject==null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final entity.externallibrary.ExternalLibraryDB newEcore =  new entity.externallibrary.ExternalLibraryDB();
		newEcore.filename = ecore.getfilename();
		newEcore.extension = "ecore";
		newEcore.parent = container;
		PyroProjectDB project = null;
		
		if(container instanceof PyroFolderDB) {
			PyroFolderDB folder = (PyroFolderDB) container;
			folder.files_ExternalLibrary.add(newEcore);
			project = graphModelController.getProject(folder);
		} else if(container instanceof PyroProjectDB) {
			project = (PyroProjectDB) container;
			project.files_ExternalLibrary.add(newEcore);
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
		newEcore.persist();
		container.persist();
		
		projectWebSocket.send(project.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(project,objectCache)));
		return Response.ok(ExternalLibrary.fromEntity(newEcore,new info.scce.pyro.rest.ObjectCache())).build();

	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("remove/{id}/{parentId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("parentId") final long parentId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		//find parent
		final entity.externallibrary.ExternalLibraryDB gm = entity.externallibrary.ExternalLibraryDB.findById(id);
		final PyroFileContainerDB container = PyroFileContainerDB.findById(parentId);
		if(gm==null||container==null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		boolean deleted = false;
		PyroProjectDB project = null;
		if(container instanceof PyroFolderDB) {
			PyroFolderDB folder = (PyroFolderDB) container;
			if(folder.files_ExternalLibrary.contains(gm)){
				folder.files_ExternalLibrary.remove(gm);
				deleted = true;
				project = graphModelController.getProject(folder);
			}
		} else if(container instanceof PyroProjectDB) {
			project = (PyroProjectDB) container;
			if(project.files_ExternalLibrary.contains(gm)){
				project.files_ExternalLibrary.remove(gm);
				deleted = true;
			}
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
		if(deleted) {
			gm.delete();
			container.persist();
			projectWebSocket.send(project.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(project,objectCache)));
			return Response.ok("OK").build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
