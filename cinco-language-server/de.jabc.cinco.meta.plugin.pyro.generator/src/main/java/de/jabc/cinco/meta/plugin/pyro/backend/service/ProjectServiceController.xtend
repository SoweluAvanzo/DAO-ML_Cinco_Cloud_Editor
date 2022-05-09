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
			«FOR s:gc.projectServices»
				import «s.projectServiceEntityFQN»;
			«ENDFOR»
			
			@javax.transaction.Transactional
			@javax.ws.rs.Path("/service")
			public class ProjectServiceController {
				
				private static java.util.List<«dbTypeName»> services = new java.util.LinkedList<>();
				private static Object serviceLock = new Object();
				
				@javax.ws.rs.GET
				@javax.ws.rs.Path("list/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response list(
					@javax.ws.rs.core.Context SecurityContext securityContext
				) {
					final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
					if (user == null) {
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					info.scce.pyro.core.rest.types.ProjectServiceList list = new info.scce.pyro.core.rest.types.ProjectServiceList();
					«FOR s:gc.projectServices»
						{
							java.util.List<«s.projectServiceEntityClass»> services = «s.projectServiceEntityClass».listAll();
							«s.projectServiceImplementation» service = new «s.projectServiceImplementation»();
							String serviceName = "«s.projectServiceName»";
							boolean canExecute = service.canExecute(services);
							boolean isDisabled = service.isDisabled(services);
							if(isDisabled) {
								list.getDisabled().add(serviceName);
							}
							else if(canExecute) {
								list.getActive().add(serviceName);
							}
							else if(!isDisabled && !canExecute) {
								list.getOther().add(serviceName);
							}
						}
					«ENDFOR»
					return Response.ok(list).build();
				}
				
				@javax.ws.rs.POST
				@javax.ws.rs.Path("update/{serviceName}/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response update(
					@javax.ws.rs.core.Context SecurityContext securityContext,
					@javax.ws.rs.PathParam("serviceName") final String serviceName,
					Object obj
				) {
					final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
					if (user == null) {
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					return handleService(serviceName, obj, (serviceInput) -> {
						// for each service-type
						«FOR s:gc.projectServices SEPARATOR " else "
						»if(serviceName.equals("«s.projectServiceName»")) {
							«s.projectServiceEntityClass» s = «s.projectServiceEntityClass».findAll().firstResult();
							«FOR attr : s.projectServiceAttributes»
								s.«attr.escapeJava» = serviceInput.inputs.get("«attr.escapeJava»").toString();
							«ENDFOR»
							s.persist();
							services.forEach((srvc) -> {
								if(srvc.id == s.id) {
									srvc = s;
								}
							});
						}«
						ENDFOR»
					});
				}
				
				@javax.ws.rs.POST
				@javax.ws.rs.Path("check/{serviceName}/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response check(
					@javax.ws.rs.core.Context SecurityContext securityContext,
					@javax.ws.rs.PathParam("serviceName") final String serviceName,
					Object obj
				) {
					final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
					if (user == null) {
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					return handleService(serviceName, obj, (serviceInput) -> {
						// just checking
					});
				}
				
				@SuppressWarnings("unchecked")
				@javax.ws.rs.POST
				@javax.ws.rs.Path("trigger/{serviceName}/private")
				@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
				@org.jboss.resteasy.annotations.GZIP
				@javax.annotation.security.RolesAllowed("user")
				public Response trigger(
					@javax.ws.rs.core.Context SecurityContext securityContext,
					@javax.ws.rs.PathParam("serviceName") final String serviceName,
					Object obj
				) {
					final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
					if (user == null) {
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					return handleService(serviceName, obj, (serviceInput) -> {
						// for each service-type
						«FOR s:gc.projectServices SEPARATOR " else "
						»if(serviceName.equals("«s.projectServiceName»")) {
							«s.projectServiceEntityClass» s = new «s.projectServiceEntityClass»();
							«FOR attr : s.projectServiceAttributes»
								s.«attr.escapeJava» = serviceInput.inputs.get("«attr.escapeJava»").toString();
							«ENDFOR»
							s.persist();
							serviceInput.service.execute(s);
						}«
						ENDFOR»
					});
				}
				
				Response handleService(String serviceName, Object obj, @SuppressWarnings("rawtypes") java.util.function.Consumer<ServiceInput> action) {
					Map<String,String> inputs = new HashMap<>();
					try {
						// identify service
						«FOR s:gc.projectServices SEPARATOR " else "
						»if(serviceName.equals("«s.projectServiceName»")) {
							«s.projectServiceImplementation» service = new «s.projectServiceImplementation»();
							«s.projectServiceRestFQN» req;
							if(obj instanceof «s.projectServiceRestFQN») {
								req = («s.projectServiceRestFQN») obj;
							} else {
								return Response.status(Response.Status.BAD_REQUEST).build();
							}
							// collect inputs for service
							«FOR attr : s.projectServiceAttributes»
								inputs.put("«attr.escapeJava»", req.get«attr.escapeJava»());
							«ENDFOR»
							// handle service execution
							synchronized(serviceLock) {
								// check if service can be executed
								{
									java.util.List<«s.projectServiceEntityClass»> _services = services.stream()
										.filter(e -> e instanceof «s.projectServiceEntityClass»)
										.map(«s.projectServiceEntityClass».class::cast)
										.collect(java.util.stream.Collectors.toList());
									boolean isValid = service.isValid(inputs, _services);
									if(!isValid) {
										return Response.status(Response.Status.NOT_ACCEPTABLE).build();
									}
								}
								// persisting active service
								{
									«s.projectServiceEntityClass» s = «s.projectServiceEntityClass».findAll().firstResult();
									if(s == null) {
										s = new «s.projectServiceEntityClass»();
									}
									«FOR attr : s.projectServiceAttributes»
										s.«attr.escapeJava» = req.get«attr.escapeJava»();
									«ENDFOR»
									s.persist();
									action.accept(new ServiceInput<>(service, inputs));
								}
							}
						}«
						ENDFOR»
					} catch(Exception e) {
						e.printStackTrace();
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
					return Response.ok().build();
				}
				
				class ServiceInput <E extends io.quarkus.hibernate.orm.panache.PanacheEntity> {
					info.scce.pyro.api.PyroProjectService<E> service;
					Map<String, String> inputs;
					
					public ServiceInput(
						info.scce.pyro.api.PyroProjectService<E> service,
						Map<String, String> inputs
					) {
						this.service = service;
						this.inputs = inputs;
					}
				}
			}
		'''
	}
	
}