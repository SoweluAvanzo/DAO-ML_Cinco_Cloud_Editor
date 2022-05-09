package de.jabc.cinco.meta.plugin.pyro.backend.core

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class GraphModelControllerGenerator extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def fileNameDispatcher() '''GraphModelController.java'''

	def contentDispatcher() '''
		package info.scce.pyro.core;
		
		import info.scce.pyro.core.rest.types.GraphModelProperty;
		import info.scce.pyro.sync.GraphModelWebSocket;
		import info.scce.pyro.sync.WebSocketMessage;
		import io.quarkus.hibernate.orm.panache.PanacheEntity;	
		import javax.ws.rs.WebApplicationException;
		import javax.ws.rs.core.Response;
		import java.util.Optional;
		import java.util.List;
		import java.util.ArrayList;
		import java.util.Arrays;
		import java.util.HashMap;
		import javax.ws.rs.core.SecurityContext;
		«FOR gm:gc.graphMopdels.filter[!isAbstract]»
			import «gm.controllerFQN»;
		«ENDFOR»
		
		
		@javax.transaction.Transactional
		@javax.ws.rs.Path("/graph")
		@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
		@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
		public class GraphModelController {
		
			@javax.inject.Inject
			info.scce.pyro.rest.ObjectCache objectCache;
		
		    @javax.inject.Inject
		    GraphModelWebSocket graphModelWebSocket;
			«FOR gm:gc.graphMopdels.filter[!isAbstract]»
				
				@javax.inject.Inject
				«gm.controllerName» «gm.name.lowEscapeJava»Controller;
			«ENDFOR»

			public void checkPermission(SecurityContext securityContext) {
		        final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				if(user != null){
		            return;
		        }
		        throw new WebApplicationException(Response.Status.FORBIDDEN);
		    }
		    
		    @javax.ws.rs.GET
		    @javax.ws.rs.Path("list/private")
		    @javax.annotation.security.RolesAllowed("user")
		    public Response listGraphModels(@javax.ws.rs.core.Context SecurityContext securityContext) {
		        final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		        
				HashMap<String, String> extensions = new HashMap<>();
				// <graphModelName, graphModelFileExtension>
				«FOR g:gc.graphMopdels.filter[!isAbstract]»
					extensions.put("«g.typeName»", "«g.fileExtension»");
				«ENDFOR»

				return Response.ok(extensions).build();
		    }
		
		    @javax.ws.rs.POST
		    @javax.ws.rs.Path("update/graphmodel/private")
		    @javax.annotation.security.RolesAllowed("user")
		    public Response updateGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, info.scce.pyro.core.graphmodel.GraphModel graphModel) {
		        final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		        //find graphmodel
		        «FOR g:gc.graphMopdels.filter[!isAbstract]»
		        {
		            final Optional<«g.entityFQN»> opt = «g.entityFQN».findByIdOptional(graphModel.getId());
		            if(opt.isPresent()) {
		                «g.entityFQN» g = opt.get();
		                checkPermission(securityContext);
		                
		                // update
		                if(graphModel.getscale()!=null){
		                    g.scale = graphModel.getscale();
		                }
		                if(graphModel.getheight()!=null){
		                    g.height = graphModel.getheight();
		                }
		                if(graphModel.getwidth()!=null){
		                    g.width = graphModel.getwidth();
		                }
		                if(graphModel.getconnector()!=null){
		                    g.connector = graphModel.getconnector();
		                }
		                if(graphModel.getrouter()!=null){
		                	g.router = graphModel.getrouter();
		                }
		                g.persist();
		                
		                graphModelWebSocket.send(g.id,WebSocketMessage.fromEntity(subject.id, GraphModelProperty.fromEntity(g)));
		                
		                return Response.ok(GraphModelProperty.fromEntity(g)).build();
		            }
		        }
				«ENDFOR»
		        return Response.status(Response.Status.NOT_FOUND).build();
		    }
			
		}

	'''
}
