package info.scce.pyro.core;

import info.scce.pyro.externallibrary.rest.ExternalLibraryList;
import info.scce.pyro.externallibrary.rest.ExternalLibrary;
import info.scce.pyro.core.rest.types.CreateEcore;
import info.scce.pyro.sync.GraphModelWebSocket;
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
	GraphModelWebSocket graphModelWebSocket;
	
	@javax.inject.Inject
	GraphModelController graphModelController;
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("read/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
		final java.util.Set<entity.externallibrary.ExternalLibraryDB> list = new java.util.HashSet<>();
				
		return Response.ok(ExternalLibraryList.fromEntity(list, objectCache))
							.build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createEcore(@javax.ws.rs.core.Context SecurityContext securityContext, CreateEcore ecore) {

		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		if(subject==null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final entity.externallibrary.ExternalLibraryDB newEcore =  new entity.externallibrary.ExternalLibraryDB();
		newEcore.filename = ecore.getfilename();
		newEcore.extension = "ecore";
		
		newEcore.persist();
		
		return Response.ok(ExternalLibrary.fromEntity(newEcore,new info.scce.pyro.rest.ObjectCache())).build();
	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("remove/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("parentId") final long parentId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		//find parent
		final entity.externallibrary.ExternalLibraryDB gm = entity.externallibrary.ExternalLibraryDB.findById(id);
		if(gm==null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		gm.delete();
		return Response.ok("OK").build();
	}
}
