package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.controller

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import org.eclipse.emf.ecore.EPackage

class EcoreController extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(EPackage p)'''«p.name.fuEscapeJava»Controller.java'''
	
	def content(EPackage p)
	'''
	package info.scce.pyro.core;
	
	import info.scce.pyro.«p.name.lowEscapeJava».rest.«p.name.fuEscapeJava»List;
	import info.scce.pyro.«p.name.lowEscapeJava».rest.«p.name.fuEscapeJava»;
	import entity.core.PyroFolderDB;
	import entity.core.PyroProjectDB;
	import entity.core.PyroFileContainerDB;
	import info.scce.pyro.core.rest.types.CreateEcore;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import info.scce.pyro.sync.ProjectWebSocket;
	import info.scce.pyro.sync.WebSocketMessage;
	import javax.ws.rs.core.SecurityContext;
	import «dbTypeFQN»;
	import javax.ws.rs.WebApplicationException;
	
	import info.scce.pyro.plugin.rest.TreeViewRest;
	
	import javax.ws.rs.core.Response;
	import java.io.IOException;
	
	@javax.transaction.Transactional
	@javax.ws.rs.Path("/«p.name.lowEscapeJava»")
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@javax.enterprise.context.RequestScoped
	public class «p.name.fuEscapeJava»Controller {
	
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
			final java.util.Set<«p.entityFQN»> list = new java.util.HashSet<>();
					
			return Response.ok(«p.name.fuEscapeJava»List.fromEntity(list, objectCache))
								.build();
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
			
			final «p.entityFQN» newEcore =  new entity.«p.name.lowEscapeJava».«p.name.fuEscapeJava»DB();
			newEcore.filename = ecore.getfilename();
			newEcore.extension = "ecore";
			newEcore.parent = container;
			
			
			newEcore.persist();
			container.persist();
			
			return Response.ok(«p.name.fuEscapeJava».fromEntity(newEcore,new info.scce.pyro.rest.ObjectCache())).build();

		}
	
		@javax.ws.rs.GET
		@javax.ws.rs.Path("remove/{id}/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("parentId") final long parentId) {
			final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
			//find parent
			final «p.entityFQN» gm = entity.«p.name.lowEscapeJava».«p.name.fuEscapeJava»DB.findById(id);
			if(gm==null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			
			gm.delete();
			container.persist();
			return Response.ok("OK").build();
		}
	}
	'''
}
