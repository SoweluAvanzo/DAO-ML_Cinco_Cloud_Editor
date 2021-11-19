package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.controller

import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.command.GraphModelCommandExecuter
import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import java.io.File
import java.util.Map
import mgl.GraphModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import style.NodeStyle
import style.Styles
import mgl.MGLModel
import mgl.Edge

class GraphModelController extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»Controller.java'''
	
	
	
	def content(GraphModel g, Styles styles, Map<String,Iterable<File>> staticGenerationFiles) {
		val modelPackage = g.modelPackage as MGLModel
		val hasAppearanceProviders = g.hasAppearanceProvider(styles) 
		val hasChecks = g.hasChecks
		val primeModels = g.resolveAllPrimeReferencedGraphModels
			.filter[gr|!gr.equals(g)] // except this one
			.groupBy[name].entrySet.map[value.get(0)];
	'''
	package info.scce.pyro.core;
	
	import info.scce.pyro.core.command.types.*;
	import info.scce.pyro.core.rest.types.*;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import info.scce.pyro.sync.WebSocketMessage;
	import javax.ws.rs.core.SecurityContext;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	
	import javax.ws.rs.core.Response;
	
	import «modelPackage.typeRegistryFQN»;
	import «g.commandExecuterFQN»;
	import «g.apiFactoryFQN»;
	
	«FOR gpr:primeModels»
		import «gpr.commandExecuterFQN»;
	«ENDFOR»
	«FOR gpr:primeModels»
		import «gpr.controllerFQN»;
	«ENDFOR»
	
	@javax.transaction.Transactional
	@javax.ws.rs.Path("/«g.name.lowEscapeJava»")
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@javax.enterprise.context.RequestScoped
	public class «g.name.fuEscapeJava»Controller {
	
		
		@javax.inject.Inject
		GraphModelWebSocket graphModelWebSocket;
		
		@javax.inject.Inject
		GraphModelController graphModelController;
	
		@javax.inject.Inject
		info.scce.pyro.rest.ObjectCache objectCache;
		
		@javax.inject.Inject
		info.scce.pyro.core.FileController fileController;
		«FOR gpr:primeModels»
			
			@javax.inject.Inject
			«gpr.controllerName» primeGraph«gpr.name.fuEscapeJava»Controller;
		«ENDFOR»
	
		@javax.ws.rs.POST
		@javax.ws.rs.Path("create/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response createGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, CreateGraphModel graph) {
	
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
	        
	        if(user==null){
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
	
		    final «g.entityFQN» newGraph =  new «g.entityFQN»();
		    newGraph.filename = graph.getfilename();
	        «g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,newGraph, new java.util.LinkedList<>());
	        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		    «new GraphModelCommandExecuter(gc).setDefault('''newGraph''',g,true, '''container''')»
		    newGraph.extension = "«g.fileExtension»";
	        newGraph.persist();

			«IF (g as ModelElement).hasPostCreateHook»
				«g.apiFactory».eINSTANCE.warmup(executer);
				«g.apiFQN» ce = new «g.apiImplFQN»(newGraph,executer);
				«(g as ModelElement).postCreateHook» ca = new «(g as ModelElement).postCreateHook»();
				ca.init(executer);
				«g.apiFQN» newGraphApi = («g.apiFQN») «typeRegistryName».getDBToApi(newGraph, executer);
				ca.postCreate(newGraphApi);
			«ENDIF»
			return Response.ok(«g.restFQN».fromEntity(newGraph,new info.scce.pyro.rest.ObjectCache())).build();

		}

		@javax.ws.rs.GET
		@javax.ws.rs.Path("read/{id}/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
			final «g.entityFQN» graph = «g.entityFQN».findById(id);
			if (graph == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			return Response.ok(«g.restFQN».fromEntity(graph, objectCache))
					.build();
		}
		«IF !g.nodes.filter[prime].filter[hasJumpToAnnotation].empty»
			
			@javax.ws.rs.GET
			@javax.ws.rs.Path("jumpto/{id}/{elementid}/private")
			@javax.annotation.security.RolesAllowed("user")
			public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id, @javax.ws.rs.PathParam("elementid") long elementId) {
				
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
														
				final «g.entityFQN» graph = «g.entityFQN».findById(id);
				if (graph == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				final «dbTypeName» node = «typeRegistryName».findById(elementId);
				
				if(node == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				
				
				String graphModelType = null;
				String elementType = null;
				String primeGraphModelId = null;
				String primeElementlId = null;
				
				«val primeReferences = g.primeReferencedElements.filter[hasJumpToAnnotation].filter(Node)»
				«FOR n:primeReferences SEPARATOR " else "
				»if(node instanceof «n.entityFQN») {
					«{
						val primeNode = (n as Node).primeReference
						val refType = primeNode.type
						'''
							final «n.apiFQN» apiNode = («n.apiFQN») «typeRegistryName».getDBToApi(node,executer);
							«refType.apiFQN» primeNode = apiNode.get«n.primeReference.name.fuEscapeJava»();
							if(primeNode != null) {
								primeElementlId = primeNode.getId();
								«IF refType instanceof GraphModel»
									primeGraphModelId = primeElementlId;
								«ELSE»
									graphmodel.ModelElementContainer mec = primeNode.getRootElement();
									primeGraphModelId = mec.getId();
								«ENDIF»
								graphModelType = «typeRegistryName».getName(mec);
								elementType = «typeRegistryName».getName(primeNode);
							}
						'''
					}»
				}«
				ENDFOR»
				
				if(graphModelType == null || elementType == null)
					return Response.status(Response.Status.BAD_REQUEST).build();
				
				info.scce.pyro.message.JumpToPrimeAnswer resp = new info.scce.pyro.message.JumpToPrimeAnswer();
				resp.setGraphModelId(primeGraphModelId);
				resp.setElementId(primeElementlId);
				resp.setGraphModelType(graphModelType);
				resp.setElementType(elementType);
				
				return Response.ok(resp).build();
			}
		«ENDIF»

		«IF hasChecks»
			
			@javax.ws.rs.GET
			@javax.ws.rs.Path("checks/{id}/private")
			@javax.annotation.security.RolesAllowed("user")
			public Response check(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
			
				final «g.entityFQN» graph = «g.entityFQN».findById(id);
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				if (graph == null || user == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
				«g.apiFactory».eINSTANCE.warmup(executer);
				
				«g.apiFQN» cgraph = new «g.apiImplFQN»(graph,executer);
				
				final java.util.Map<graphmodel.IdentifiableElement,info.scce.pyro.core.command.types.CheckResultCommand> results = new java.util.HashMap<>();
				
				//do check
				final «g.mcamExecutionFQN» exec = new «g.mcamExecutionFQN»();
				final «g.mcamAdapterFQN» adapter = exec.initApiAdapter(cgraph);
				exec.getCheckModuleRegistry().forEach((n)->{
					n.init();
					n.execute(adapter).forEach((e,i)->{
						graphmodel.IdentifiableElement element = (graphmodel.IdentifiableElement) e;
						info.scce.pyro.core.command.types.CheckResultCommand crc = info.scce.pyro.core.command.types.CheckResultCommand.fromElement(element);
						if(results.containsKey(element)) {
							crc = results.get(element);
						}
						info.scce.pyro.core.command.types.CheckResultCommand crcFinal = crc;
						«g.mcamAdapterIdFQN» adapterId = («g.mcamAdapterIdFQN») i;
						adapterId.getErrors().forEach(m->crcFinal.addResult(m,"error"));
						adapterId.getWarnings().forEach(m->crcFinal.addResult(m,"warning"));
						adapterId.getInfos().forEach(m->crcFinal.addResult(m,"info"));
						if(!crcFinal.getResults().isEmpty()){
							results.put(element,crcFinal);
						}
					});
				});
				
				return Response.ok(results.values()).build();
			}
		«ENDIF»
		«IF hasAppearanceProviders»
			
			@javax.ws.rs.GET
			@javax.ws.rs.Path("appearance/{id}/private")
			@javax.annotation.security.RolesAllowed("user")
			public Response appearance(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
			
				final «g.entityFQN» graph = «g.entityFQN».findById(id);
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				if (graph == null || user == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				
				//setup batch execution
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
				«g.apiFactory».eINSTANCE.warmup(executer);
				
				//update appearance
				executer.updateAppearance();
				
				// propagate
				return createResponse("basic_valid_answer", executer,
						user.id, graph.id, java.util.Collections.emptyList());
			}
		«ENDIF»
		«IF g.generating»
			
			@javax.ws.rs.GET
			@javax.ws.rs.Path("generate/{id}/{generatorId}/private")
			@javax.annotation.security.RolesAllowed("user")
			public Response generate(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id, @javax.ws.rs.PathParam("generatorId") String generatorId) {
			
				final «g.entityFQN» graph = «g.entityFQN».findById(id);
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				if (graph == null || user == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				//setup batch execution
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
				«g.apiFactory».eINSTANCE.warmup(executer);
				«g.apiFQN» cgraph = new «g.apiImplFQN»(graph,executer);
				
				«FOR gen:g.generators»
					«IF gen.value.length >= 3»
						if(generatorId != null && generatorId.equals("«gen.value.get(0)»")) {
					«ELSE»
						if(generatorId == null || generatorId.equals("null")) {
					«ENDIF»
						try {
							
							java.util.Map<String,String[]> staticResourecURLs = new java.util.HashMap<>();
							«FOR f:staticGenerationFiles.entrySet»
								staticResourecURLs.put("«f.key»",new String[]{
									«FOR file:f.value SEPARATOR ","»
										"«file.absolutePath.suffix(f.key)»"
									«ENDFOR»
									});
							«ENDFOR»
							«gen.value.get(0)» generator = new «gen.value.get(0)»();
							generator.generateFiles(
								cgraph,
								"«IF gen.value.size>1»«gen.value.get(1)»«ENDIF»",
								"asset/static/«g.name.lowEscapeJava»",«/* TODO: SAMI: outsource into MGLExtension against updateAnomaly */»
								staticResourecURLs,
								fileController
							);
							//TODO
							return javax.ws.rs.core.Response.ok(null).build();
						
						} catch (java.io.IOException e) {
							e.printStackTrace();
						}
					}
				«ENDFOR»
				
				return Response.status(Response.Status.EXPECTATION_FAILED).build();
			}
		«ENDIF»
		
		«IF g.interpreting»
			«/* TODO: SAMI: interpreterId analog to generators, maybe? */»
			@javax.ws.rs.GET
			@javax.ws.rs.Path("interpreter/{id}/private")
			@javax.annotation.security.RolesAllowed("user")
			public Response interpreter(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") long id) {
			
				final «g.entityFQN» graph = «g.entityFQN».findById(id);
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				if (graph == null || user == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				//setup batch execution
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
				«g.apiFactory».eINSTANCE.warmup(executer);
				«g.apiFQN» cgraph = new «g.apiImplFQN»(graph,executer);
				
				«FOR gen:g.interperters»
					«gen.value.get(0)» interpreter = new «gen.value.get(0)»();
					interpreter.init(executer);
					interpreter.runInterpreter(cgraph);
				«ENDFOR»
					
				return javax.ws.rs.core.Response.ok(null).build();
			}
		«ENDIF»
		
		@javax.ws.rs.GET
		@javax.ws.rs.Path("customaction/{id}/{elementId}/fetch/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response fetchCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId) {
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
			final «g.entityFQN» graph = «g.entityFQN».findById(id);
			if (graph == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			java.util.Map<String,String> map = new java.util.HashMap<>();
			
			«IF g.elementsAndGraphmodels.exists[hasCustomAction]»
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
				«g.apiFactory».eINSTANCE.warmup(executer);
				«dbTypeName» e = «typeRegistryName».findById(elementId);
				
				«FOR e:(g.elements.filter[!isIsAbstract]+#[g]).filter[hasCustomAction] SEPARATOR " else "
				»if(e instanceof «e.entityFQN») {
					«e.entityFQN» dbEntity = («e.entityFQN») e;
					«e.apiFQN» ce = new «e.apiImplFQN»(dbEntity,executer);
					«{
						val customActions = e.resolveCustomActions
						var i = -1
						'''
							«FOR anno:customActions»
								«{
									i=i+1
									'''
										// customAction «i»
										«anno.value.get(0)» ca«i» = new «anno.value.get(0)»();
										ca«i».init(executer);
										if(ca«i».canExecute(ce)){
											map.put("«anno.value.get(0)»",ca«i».getName());
										}
									'''
								}»
							«ENDFOR»
						'''
					}»
				}«
				ENDFOR»
				
			«ENDIF»
			return Response.ok(map).build();
		}
		
		«FOR ai:g.editorButtons.indexed»
		«{
			val a = ai.value
			'''
			@javax.ws.rs.POST
			@javax.ws.rs.Path("{id}/button/«a.value.get(1).escapeJava»/trigger/private")
			@javax.annotation.security.RolesAllowed("user")
			public Response triggerButtonActions«ai.key»(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") long id, info.scce.pyro.core.command.types.Action action) {
				
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				final «g.entityFQN» graph = «g.entityFQN».findById(id);
				if (graph == null) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				
				«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
				info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
				«g.apiFactory».eINSTANCE.warmup(executer);
				«dbTypeName» elem = «typeRegistryName».findById(elementId);
								
				factoryWarmup(pyroProject,user,executer);
				«g.apiFQN».«g.name.fuEscapeJava» ce = new «g.apiImplFQN».«g.name.fuEscapeJava»Impl(graph,executer);
				«a.value.get(0)» ca = new «a.value.get(0)»();
				ca.init(executer);
				ca.execute(ce);
				
				«IF hasAppearanceProviders»
				executer.updateAppearance();
				«ENDIF»
				
				//propagate
				Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
				propagateChange(id, user.id, response.getEntity());
				return response;
			}
			'''
		}»
		
		«ENDFOR»
		
		@javax.ws.rs.POST
		@javax.ws.rs.Path("customaction/{id}/{elementId}/trigger/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response triggerCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
			final «g.entityFQN» graph = «g.entityFQN».findById(id);
			if (graph == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
			
			«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
			info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
			«g.apiFactory».eINSTANCE.warmup(executer);
			
			«IF g.elementsAndGraphmodels.exists[hasCustomAction]»
				«dbTypeName» e = «typeRegistryName».findById(elementId);
				
				«FOR e:(g.elements.filter[!isIsAbstract]+#[g]).filter[hasCustomAction] SEPARATOR " else "
				»if(e instanceof «e.entityFQN») {
					«e.entityFQN» dbEntity = («e.entityFQN») e;
					«e.apiFQN» ce = new «e.apiImplFQN»(dbEntity,executer);
					«FOR anno:e.resolveCustomActions SEPARATOR " else "
					»if(action.getFqn().equals("«anno.value.get(0)»")) {
						«anno.value.get(0)» ca = new «anno.value.get(0)»();
						ca.init(executer);
						ca.execute(ce);
					}«
					ENDFOR»
					«IF hasAppearanceProviders»
						executer.updateAppearance();
					«ENDIF»
				}«
				ENDFOR»
				
			«ENDIF»
			Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
			return response;
		}
		

		@javax.ws.rs.POST
		@javax.ws.rs.Path("{id}/psaction/{elementId}/trigger/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response triggerPostSelectActions(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
			
			final «g.entityFQN» graph = «g.entityFQN».findById(id);
			if (graph == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
			
			«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
			info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
			«g.apiFactory».eINSTANCE.warmup(executer);

			
			«IF g.elementsAndGraphmodels.exists[hasPostSelect]»
				boolean hasExecuted = false;
				String typeName = action.getFqn();
				«FOR e:(g.elements.filter[!isIsAbstract]+#[g]).filter[hasPostSelect] SEPARATOR " else "
				»if("«e.typeName»".equals(typeName)) {
					«dbTypeName» elem = «typeRegistryName».findByType(typeName, elementId);
					«e.entityFQN» e = («e.entityFQN»)elem;
					«e.apiFQN» ce = new «e.apiImplFQN»(e,executer);
					{
						«e.postSelectHook» ca = new «e.postSelectHook»();
						ca.init(executer);
						ca.postSelect(ce);
					}
				}«
				ENDFOR»
				
				«IF hasAppearanceProviders»
					executer.updateAppearance();
				«ENDIF»
				
				Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
				//propagate
				propagateChange(id, user.id, response.getEntity());
			«ELSE»
				Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
			«ENDIF»
			return response;
		}
		
		@javax.ws.rs.POST
		@javax.ws.rs.Path("dbaction/{id}/{elementId}/trigger/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response triggerDoubleClickActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
			final «g.entityFQN» graph = «g.entityFQN».findById(id);
			if (graph == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
			
			«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
			info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
			«g.apiFactory».eINSTANCE.warmup(executer);
			boolean hasExecuted = false;
			
			«IF g.elementsAndGraphmodels.exists[hasDoubleClickAction]»
				«dbTypeName» e = «typeRegistryName».findById(elementId);
				
				«FOR e:(g.elements.filter[!isIsAbstract]+#[g]).filter[hasDoubleClickAction] SEPARATOR " else "
				»if(e instanceof «e.entityFQN») {
					«e.entityFQN» dbEntity = («e.entityFQN») e;
					«e.apiFQN» ce = new «e.apiImplFQN»(dbEntity,executer);
					«{
						val doubleClickActions = e.resolveDoubleClickActions
						var i=-1
						'''
							«FOR anno:doubleClickActions»
								«{
									i=i+1
									'''
										// doubleClickAction «i»
										«anno.value.get(0)» ca«i» = new «anno.value.get(0)»();
										ca«i».init(executer);
										if(ca«i».canExecute(ce)){
											ca«i».execute(ce);
											hasExecuted = true;
										}
									'''
								}»
							«ENDFOR»
						'''
					}»
					«IF hasAppearanceProviders»
						executer.updateAppearance();
					«ENDIF»
				}«
				ENDFOR»
				
				Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
				if(hasExecuted){
					//propagate
					propagateChange(id, user.id, response.getEntity());
				}
			«ELSE»
				Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
			«ENDIF»
			
			return response;
		}
		
		@javax.ws.rs.GET
		@javax.ws.rs.Path("remove/{id}/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
			final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
			
			//find parent
			final «g.entityFQN» gm = «g.entityFQN».findById(id);
			if(gm==null){
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			
			
			boolean succeeded = removeGraphModel(user, gm, false);
			if (!succeeded) {
				return Response.status(Response.Status.FORBIDDEN).build();
	        }
			
			«g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,gm,new java.util.LinkedList<>());
			info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
			«g.apiFactory».eINSTANCE.warmup(executer);
			removeContainer(new «g.apiImplFQN»(gm,executer));
			
			try {
				// trying to execute transaction (since deleting a graphmodel can lead to complex errors)
				gm.getEntityManager().flush();
			} catch(javax.persistence.PersistenceException e) {
				e.printStackTrace();
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			// synchronize project-structure
			return Response.ok("OK").build();
		}
		

		
		public boolean removeGraphModel(entity.core.PyroUserDB user,«g.entityFQN» gm) {
			return removeGraphModel(user, gm, true);
		}
		
		public boolean removeGraphModel(entity.core.PyroUserDB user, «g.entityFQN» gm, boolean delete) {
			
			// delete
			if(delete)
				gm.delete();
			
			return true;
		}
		
		private void removeContainer(«g.apiFQN» graph) {
			graph.delete();
		}

		@javax.ws.rs.POST
		@javax.ws.rs.Path("message/{graphModelId}/private")
		@javax.annotation.security.RolesAllowed("user")
		public Response receiveMessage(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("graphModelId") long graphModelId, Message m) {
		    final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);

		    final «g.entityFQN» graph = «g.entityFQN».findById(graphModelId);
		    if(user==null||graph==null){
		        return Response.status(Response.Status.BAD_REQUEST).build();
		    }
		
		
		    if(m instanceof CompoundCommandMessage){
		        Response response = executeCommand((CompoundCommandMessage) m, user, graph, securityContext);
				if(response.getStatus()==200){
					propagateChange(graphModelId, user.id, response.getEntity());
				}
				graph.persist();
				return response;
		    }
		    else if(m instanceof GraphPropertyMessage){
		        final GraphPropertyMessage gpm = (GraphPropertyMessage) m;
		        graph.connector = gpm.getGraph().getconnector();
		        graph.router = gpm.getGraph().getrouter();
		        graph.width = gpm.getGraph().getwidth();
		        graph.height = gpm.getGraph().getheight();
		        graph.scale = gpm.getGraph().getscale();
		        graph.persist();
		        //propagate
				propagateChange(graphModelId, user.id, m);
		        return Response.ok("OK").build();
		    }
		    else if (m instanceof PropertyMessage) {
				Response response = executePropertyUpdate((PropertyMessage) m, user,graph);
				if(response.getStatus()==200){
					propagateChange(graphModelId, user.id, response.getEntity());
				}
				return response;
			}
		
		    return Response.status(Response.Status.BAD_REQUEST).build();
		}

		private void propagateChange(long graphModelId, long senderId, Object content) {
			graphModelWebSocket.send(graphModelId,WebSocketMessage.fromEntity(senderId,content));
		}

		private Response executePropertyUpdate(PropertyMessage pm,entity.core.PyroUserDB user, «g.entityFQN» graph) {
		    «g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
		    info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		    «g.apiFactory».eINSTANCE.warmup(executer);
			
			«IF g.hasPreSave»
				{
					«g.apiImplFQN» element = new «g.apiImplFQN»(graph,executer);
					«g.getPreChange» hook = new «g.getPreChange»();
					hook.init(executer);
					hook.preSave(element);
				}
	        «ENDIF»
	        
	        String type = pm.getDelegate().get__type();
			«FOR e:g.elements.filter[!isIsAbstract] SEPARATOR " else "»
				if (type.equals("«e.typeName»")){
					«e.entityFQN» target = «e.entityFQN».findById(pm.getDelegate().getId());
					«e.apiFQN» targetAPI = new «e.apiImplFQN»(target,executer«IF e.isType»,null,null«ENDIF»);
					executer.update«e.name.escapeJava»(«IF !e.isType»targetAPI, «ENDIF»(«e.restFQN») pm.getDelegate());
				}
			«ENDFOR»
		    CompoundCommandMessage response = new CompoundCommandMessage();
			response.setType("basic_valid_answer");
			CompoundCommand cc = new CompoundCommand();
			cc.setQueue(executer.getBatch().getCommands());
			response.setCmd(cc);
			response.setGraphModelId(graph.id);
			response.setSenderId(user.id);
			response.setHighlightings(executer.getHighlightings());
			
			«IF hasAppearanceProviders»
				executer.updateAppearance();
				
			«ENDIF»
			return Response.ok(response).build();
		}
		
		public «g.commandExecuter» buildExecuter(«dbTypeName» ie,SecurityContext securityContext){
			«dbTypeName» mec = ie;
			while (mec!=null) {
				if(mec instanceof «g.entityFQN») {
					«g.entityFQN» container = («g.entityFQN») mec;
					final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
					return new «g.commandExecuter»(user, objectCache, graphModelWebSocket, container, new java.util.LinkedList<>());
				}«FOR c:g.elements.filter(NodeContainer).filter[!isAbstract]
				» else if(mec instanceof «c.entityFQN») {
					«c.entityFQN» container = («c.entityFQN») mec;
					mec = container.getContainer();
				}«
				ENDFOR»
				else {
					break;
				}
				
			}
			throw new IllegalStateException("Graphmodel could not be found");
		}
		
		private void createNode(String type,Object mec, long x, long y, Long primeId, «g.commandExecuter» executer,SecurityContext securityContext) {
			«FOR node:g.nodesTopologically.filter(NodeContainer).filter[!isIsAbstract] + #[g] SEPARATOR " else "
			»if(mec instanceof «node.apiFQN») {
				«node.apiFQN» n = («node.apiFQN») mec;
				«FOR n:node.possibleEmbeddingTypes(g).filter[!isIsAbstract] SEPARATOR " else "»
					if(type.equals("«n.typeName»")) {
						n.new«n.name.fuEscapeJava»(
						«IF n.isPrime»
							primeId,
						«ENDIF»
						(int)x,
						(int)y);
					}
				«ENDFOR»
			}«
			ENDFOR»
		}
		
		private void addBendingPoints(«dbTypeName» delegate, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions) {
			java.util.List<entity.core.BendingPointDB> bpEntities =  positions.stream().map(p -> {
			    entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
			    bp.x = p.getx();
			    bp.y = p.gety();
			    bp.persist();
			    return bp;
			}).collect(java.util.stream.Collectors.toList());
			
			«FOR e:g.edges.filter[!isAbstract] SEPARATOR " else "
			»if(delegate instanceof «e.entityFQN») {
				«e.entityFQN» edge = («e.entityFQN») delegate;
				edge.bendingPoints.clear();
				edge.bendingPoints.addAll(bpEntities);
			}«
			ENDFOR»
			
			delegate.persist();
		}
		
		private graphmodel.Edge createEdge(String type, graphmodel.Node source, graphmodel.Node target, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions, «g.commandExecuter» executer) {
			graphmodel.Edge edge = null;
			
			«FOR source:g.nodesTopologically.filter[!isIsAbstract].filter[!possibleOutgoing.empty] SEPARATOR " else "
			»if(source instanceof «source.apiFQN») {
				«FOR edge:source.possibleOutgoing.filter[!isIsAbstract] SEPARATOR " else "»
					if(type.equals("«edge.typeName»")) {
						if(
							«FOR target:edge.possibleTargets.filter[!isIsAbstract] SEPARATOR "\n|| "
							»target instanceof «target.apiFQN»«
							ENDFOR»
						) {
							edge = executer.create«edge.name.fuEscapeJava»(source, target, positions, null);
						}
					}
				«ENDFOR»
			}«
			ENDFOR»
			
			return edge;
		}
		
	    private Response executeCommand(CompoundCommandMessage ccm, entity.core.PyroUserDB user, «g.entityFQN» graph,SecurityContext securityContext) {
	        //setup batch execution
	        «g.commandExecuter» executer = new «g.commandExecuter»(user,objectCache,graphModelWebSocket,graph,ccm.getHighlightings());
	        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	        «g.apiFactory».eINSTANCE.warmup(executer);
	        
	        //execute command
	        try{
	        	boolean isReOrUndo = ccm.getType().contains("redo")||ccm.getType().contains("undo");
				«IF g.hasPreSave»
					{
						«g.apiImplFQN» element = new «g.apiImplFQN»(graph,executer);
						«g.getPreChange» hook = new «g.getPreChange»();
						hook.init(executer);
						hook.preSave(element);
					}
		        «ENDIF»
		        for(Command c:ccm.getCmd().getQueue()){

		        	// CREATE NODE COMMAND
		            if(c instanceof CreateNodeCommand){
		                CreateNodeCommand cm = (CreateNodeCommand) c;
		                long containerId = resolveId(cm.getContainerId(), ccm.getRewriteRule());
		                
		                // resolving container
						graphmodel.ModelElementContainer cmec = (graphmodel.ModelElementContainer) «typeRegistryName».findApiByType(cm.getContainerType(), containerId, executer);
						graphmodel.Node n = null;
						
						// creating node
						if(isReOrUndo) {
							if(cm.getDelegateId()!=0){
								«FOR e:g.nodesTopologically.filter[!isIsAbstract] SEPARATOR " else "
								»if(c.getType().equals("«e.typeName»")) {
									n = executer.create«e.name.escapeJava»(
										cm.getX(),
										cm.getY(),
										cm.getWidth(),
										cm.getHeight(),
										cmec,
										(«e.restFQN») cm.getElement()«IF e.prime»,
										cm.getPrimeId()«ENDIF»
									);
									ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
								}«
								ENDFOR»
							} else {
								«FOR e:g.nodesTopologically.filter[!isIsAbstract] SEPARATOR " else "
								»if(c.getType().equals("«e.typeName»")) {
									executer.create«e.name.escapeJava»(
										cm.getX(),
										cm.getY(),
										«{
											val nodeStyle = styling(e,styles) as NodeStyle
											val size = nodeStyle.mainShape.size
											'''
												«IF size!==null»
													«size.width»,
													«size.height»,
												«ELSE»
													«MGLExtension.DEFAULT_WIDTH»,
													«MGLExtension.DEFAULT_HEIGHT»,
												«ENDIF»
											'''
										}»
										cmec,
										(«e.restFQN»)cm.getElement()«IF e.prime»,
										cm.getPrimeId()
										«ENDIF»);
								}«
								ENDFOR»
							}
						} else {
							createNode(c.getType(),cmec,cm.getX(),cm.getY(),cm.getPrimeId(),executer, securityContext);
						}
					}
					
					// MOVE NODE COMMAND
					else if(c instanceof MoveNodeCommand){
						MoveNodeCommand cm = (MoveNodeCommand) c;
						long containerId = resolveId(cm.getContainerId(), ccm.getRewriteRule());
						long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
						
						// resolving elements
						graphmodel.ModelElementContainer cmec = (graphmodel.ModelElementContainer) «typeRegistryName».findApiByType(cm.getContainerType(), containerId, executer);
						graphmodel.Node cn = (graphmodel.Node) «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
						
						// move node
						cn.moveTo(cmec,java.lang.Math.toIntExact(cm.getX()), java.lang.Math.toIntExact(cm.getY()));
					}
					
					// RESIZE NODE COMMAND
					else if(c instanceof ResizeNodeCommand){
						ResizeNodeCommand cm = (ResizeNodeCommand) c;
						long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
						
						// resolving elements
						graphmodel.Node cn = (graphmodel.Node) «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
						
						cn.resize(java.lang.Math.toIntExact(cm.getWidth()), java.lang.Math.toIntExact(cm.getHeight()));
					}
					
					// REMOVE NODE COMMAND
					else if(c instanceof RemoveNodeCommand){
						RemoveNodeCommand cm = (RemoveNodeCommand) c;
						long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
						
						// resolving elements
						graphmodel.Node cn = (graphmodel.Node) «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
						
						// delete node
						cn.delete();
					}

		            // CREATE EDGE COMMAND
		            else if(c instanceof CreateEdgeCommand){
		                CreateEdgeCommand cm = (CreateEdgeCommand) c;
		                long sourceId = resolveId(cm.getSourceId(), ccm.getRewriteRule());
		                long targetId = resolveId(cm.getTargetId(), ccm.getRewriteRule());

						// resolving elements
						graphmodel.Node source = (graphmodel.Node) «typeRegistryName».findApiByType(cm.getSourceType(), sourceId, executer);
						graphmodel.Node target = (graphmodel.Node) «typeRegistryName».findApiByType(cm.getTargetType(), targetId, executer);
						
						// resolving and creating edge
						if(isReOrUndo) {
							if(cm.getDelegateId()!=0){
								graphmodel.Edge e = createEdge(c.getType(),source,target,cm.getPositions(),executer);
								ccm.rewriteId(cm.getDelegateId(),e.getDelegateId());
							} else {
								createEdge(c.getType(),source,target,cm.getPositions(),executer);
							}
						} else {
							createEdge(c.getType(),source,target,cm.getPositions(),executer);
						}
					}

					// RECONNECT EDGE COMMAND
					else if(c instanceof ReconnectEdgeCommand){
						ReconnectEdgeCommand cm = (ReconnectEdgeCommand) c;
						long sourceId = resolveId(cm.getSourceId(), ccm.getRewriteRule());
						long targetId = resolveId(cm.getTargetId(), ccm.getRewriteRule());
						long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
						
						// resolving elements
						graphmodel.Node source = (graphmodel.Node) «typeRegistryName».findApiByType(cm.getSourceType(), sourceId, executer);
						graphmodel.Node target = (graphmodel.Node) «typeRegistryName».findApiByType(cm.getTargetType(), targetId, executer);
						graphmodel.Edge ce = (graphmodel.Edge) «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
						
		                // reconnecting edge
		                if(!ce.getTargetElement().equals(target)) {
		                	// target changed
		                	ce.reconnectTarget(target);
		                } else {
		                	// source changed
		                	ce.reconnectSource(source);
		                }
		            }

		            // REMOVE EDGE COMMAND
		            else if(c instanceof RemoveEdgeCommand){
		                RemoveEdgeCommand cm = (RemoveEdgeCommand) c;
		                long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
		                
		                // resolving elements
		                graphmodel.Edge ce = (graphmodel.Edge) «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
		               	
		               	// delete edge
	                	ce.delete();
		            }
					
					// UPDATE BEND POINT COMMAND
					else if(c instanceof UpdateBendPointCommand){
						UpdateBendPointCommand cm = (UpdateBendPointCommand) c;
						long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
						
						// resolving elements
						graphmodel.Edge ce = (graphmodel.Edge) «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
						
						// updated edge
						«FOR e:g.edgesTopologically.filter[!isIsAbstract] SEPARATOR " else " 
						»if(ce instanceof «e.apiFQN») {
							«e.apiFQN» apiEdge = («e.apiFQN») ce;
							executer.update«e.name.escapeJava»(apiEdge, cm.getPositions());
						}«
						ENDFOR»
						
						// persist update
						«dbTypeName» edge = «typeRegistryName».getApiToDB(ce); 
						edge.persist();
		            }

		            // UPDATE COMMAND (RE OR UNDO)
		            else if(c instanceof UpdateCommand && isReOrUndo){
		            	UpdateCommand cm = (UpdateCommand) c;
		                long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
						
						// resolving elements
						graphmodel.IdentifiableElement ce = «typeRegistryName».findApiByType(c.getType(), delegateId, executer);
						
						// update element
						«FOR e:g.elementsAndGraphmodels.filter[!isIsAbstract] SEPARATOR " else " 
						»if(ce instanceof «e.apiFQN») {
							executer.update«e.name.fuEscapeJava»(
								«IF !e.isType»
									(«e.apiFQN») ce,
								«ENDIF»
								(«e.restFQN») cm.getElement()
							);
						}«
						ENDFOR»
					}
		            else {
						return Response.status(Response.Status.BAD_REQUEST).build();
		            }
		        }
				«IF hasAppearanceProviders»
					executer.updateAppearance();
				«ENDIF»
	        } catch(Exception e) {
	        	//send rollback
	        	e.printStackTrace();
	        	java.util.List<Command> reversed = new java.util.LinkedList<>(ccm.getCmd().getQueue());
				java.util.Collections.reverse(reversed);
	        	ccm.getCmd().setQueue(reversed);
	        	if(ccm.getType().equals("basic")){
					ccm.setType("basic_invalid_answer");
				} else if(ccm.getType().equals("undo")){
					ccm.setType("undo_invalid_answer");
				} else if(ccm.getType().equals("redo")){
					ccm.setType("redo_invalid_answer");
				}
	        	return Response.ok(ccm).build();
	        }
			String type = "";
			if(ccm.getType().equals("basic")){
				type = "basic_valid_answer";
			} else if(ccm.getType().equals("undo")){
				type = "undo_valid_answer";
			} else if(ccm.getType().equals("redo")){
				type = "redo_valid_answer";
			}
			return createResponse(type,executer,user.id,graph.id,ccm.getRewriteRule());
	    }
	    
		private long resolveId(long id, java.util.List<info.scce.pyro.core.command.types.RewriteRule> rewriteRules) {
			java.util.Optional<info.scce.pyro.core.command.types.RewriteRule> rewrittenRule = 
					rewriteRules.stream()
					.filter( (e) -> id == e.getOldId() ).findFirst();
			if(rewrittenRule.isPresent()) {
				return rewrittenRule.get().getNewId();
			} else
				return id;
		}
	    
	    private Response createResponse(String type,«g.commandExecuter» executer,long userId,long graphId,java.util.List<RewriteRule> rewriteRuleList) {
	       	CompoundCommandMessage response = new CompoundCommandMessage();
	   		response.setType(type);
	   		response.setRewriteRule(rewriteRuleList);
	   		CompoundCommand cc = new CompoundCommand();
	   		cc.setQueue(executer.getBatch().getCommands());
	   		response.setCmd(cc);
	   		response.setOpenFile(executer.getOpenFileCommand());
	   		response.setGraphModelId(graphId);
	   		response.setSenderId(userId);
	   		response.setHighlightings(executer.getHighlightings());
	   		return Response.ok(response).build();
	    }
	    
	}
	
	'''
	}
	
	def suffix(String absolutPath, String resourceFolder) {
		absolutPath.substring(absolutPath.lastIndexOf(resourceFolder)+resourceFolder.length+1)
	}
}
