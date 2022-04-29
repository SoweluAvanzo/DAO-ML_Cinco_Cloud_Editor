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
			
			private static java.util.List<«dbTypeName»> services = new java.util.LinkedList();
			private static Object serviceLock = new Object();

			@javax.inject.Inject
			private GraphModelController graphModelController;
			
			@javax.ws.rs.GET
			@javax.ws.rs.Path("list/{id}/private")
			@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
			@org.jboss.resteasy.annotations.GZIP
			@javax.annotation.security.RolesAllowed("user")
			public Response list(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
				graphModelController.checkPermission(securityContext);
				final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
				
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

				return Response.ok(list).build();
			}
			
			«/* TODO: SAMI: ProjectServices currently untested */»
			«FOR s:gc.projectServices»
				
				@javax.ws.rs.POST
				@javax.ws.rs.Path("trigger/«s.value.get(1).escapeJava»/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response trigger«s.value.get(1).fuEscapeJava»(@javax.ws.rs.core.Context SecurityContext securityContext, info.scce.pyro.service.rest.«s.value.get(1).fuEscapeJava» req) {
					graphModelController.checkPermission(securityContext);
					final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
					
					try {
						«s.value.get(0)» service = new «s.value.get(0)»();
						Map<String,String> inputs = new HashMap<>();
						«FOR attr:s.value.subList(2,s.value.size)»
							inputs.put("«attr»", req.get«attr.fuEscapeJava»());
						«ENDFOR»

						synchronized(serviceLock) {
							boolean isValid = service.isValid(inputs, services);
							if(!isValid) {
								return Response.status(Response.Status.BAD_REQUEST).build();
							}
							
							«s.projectServiceClassName» s = new «s.projectServiceClassName»();
							«FOR attr:s.value.subList(2,s.value.size)»
								s.«attr.fuEscapeJava» = req.get«attr.fuEscapeJava»();
							«ENDFOR»	
							s.persist();
							
							services.add(s);
						}

						service.execute(s);
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