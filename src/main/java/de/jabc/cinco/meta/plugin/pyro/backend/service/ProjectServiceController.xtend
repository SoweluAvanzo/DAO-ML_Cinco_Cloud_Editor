package de.jabc.cinco.meta.plugin.pyro.backend.service

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class ProjectServiceController extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename()'''ProjectServiceController.java'''
	
	
	
	def content() {
	'''
		package info.scce.pyro.service;
		
		import java.util.Map;
		import java.util.HashMap;
		
		import javax.ws.rs.core.Response;
		import javax.ws.rs.core.SecurityContext;
		
		import «dbTypeFQN»;
		import info.scce.pyro.core.GraphModelController;
		«FOR s:gc.projectServices»
			import «s.projectServiceFQN»;
		«ENDFOR»
		
		@javax.transaction.Transactional
		@javax.ws.rs.Path("/service")
		public class ProjectServiceController {
			
			@javax.inject.Inject
			private GraphModelController graphModelController;
			
			@javax.ws.rs.GET
			@javax.ws.rs.Path("list/{id}/private")
			@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
			@org.jboss.resteasy.annotations.GZIP
			@javax.annotation.security.RolesAllowed("user")
			public Response list(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
				final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(id);
				
				if(project==null){
				    return Response.status(Response.Status.BAD_REQUEST).build();
				}
				graphModelController.checkPermission(project,securityContext);
				
				info.scce.pyro.core.rest.types.ProjectServiceList list = new info.scce.pyro.core.rest.types.ProjectServiceList();
				java.util.List<«dbTypeName»> services = «dbTypeName».list();
				
				«FOR s:gc.projectServices»
					{
						«s.value.get(0)» service = new «s.value.get(0)»();
						boolean canExecute = service.canExecute(services);
						if(canExecute) {
							list.getActive().add("«s.value.get(1).escapeJava»");
						}
						boolean isDisabled = service.isDisabled(services);
						if(isDisabled) {
							list.getDisabled().add("«s.value.get(1).escapeJava»");
						}
					}
				«ENDFOR»
				«FOR s:gc.projectActions»
					{
						«s.value.get(0)» service = new «s.value.get(0)»();
						boolean canExecute = service.canExecute(project);
						if(canExecute) {
							list.getActive().add("«s.value.get(1).escapeJava»");
						}
					}
				«ENDFOR»
				return Response.ok(list).build();
			}
			«FOR s:gc.projectServices»
				
				@javax.ws.rs.POST
				@javax.ws.rs.Path("trigger/«s.value.get(1).escapeJava»/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response trigger«s.value.get(1).fuEscapeJava»(@javax.ws.rs.core.Context SecurityContext securityContext, info.scce.pyro.service.rest.«s.value.get(1).fuEscapeJava» req) {
					final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
					
					graphModelController.checkPermission(securityContext);
					
					try {
						«s.value.get(0)» service = new «s.value.get(0)»();
						Map<String,String> inputs = new HashMap<>();
						«FOR attr:s.value.subList(2,s.value.size)»
							inputs.put("«attr»", req.get«attr.fuEscapeJava»());
						«ENDFOR»
						java.util.List<«dbTypeName»> services = project.getProjectServices().stream()
							.collect(java.util.stream.Collectors.toList());
						boolean isValid = service.isValid(inputs, services); «/* TODO: SAMI: all projectServices? or only from that type? */»
						if(!isValid) {
							return Response.status(Response.Status.BAD_REQUEST).build();
						}
						
						«s.projectServiceClassName» s = new «s.projectServiceClassName»();
						«FOR attr:s.value.subList(2,s.value.size)»
							s.«attr.fuEscapeJava» = req.get«attr.fuEscapeJava»();
						«ENDFOR»	
						s.persist();
						
						
						service.execute(s); «/* TODO: SAMI: why are services persisted? how will they be deleted? */»
					} catch(Exception e) {
						e.printStackTrace();
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					return Response.ok().build();
				}
			«ENDFOR»
			«FOR s:gc.projectActions»
				
				@javax.ws.rs.GET
				@javax.ws.rs.Path("triggeraction/«s.value.get(1).escapeJava»/{id}/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response triggerAction«s.value.get(1).fuEscapeJava»(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
					final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
					final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(id);
					
					if(project==null){
					    return Response.status(Response.Status.BAD_REQUEST).build();
					}
					graphModelController.checkPermission(project,securityContext);
					
					try {
						«s.value.get(0)» action = new «s.value.get(0)»();
						if(action.canExecute(project)) {
							action.execute(project);
						}
					} catch(Exception e) {
						e.printStackTrace();
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					return Response.ok().build();
				}
			«ENDFOR»
		}
	'''
	}
	
}