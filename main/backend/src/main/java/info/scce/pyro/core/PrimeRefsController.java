package info.scce.pyro.core;

import info.scce.pyro.core.command.types.*;
import info.scce.pyro.core.rest.types.*;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.sync.ProjectWebSocket;
import info.scce.pyro.sync.WebSocketMessage;
import javax.ws.rs.core.SecurityContext;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import info.scce.cinco.product.primerefs.primerefs.util.TypeRegistry;
import info.scce.pyro.core.command.PrimeRefsCommandExecuter;
import info.scce.cinco.product.primerefs.primerefs.PrimeRefsFactory;

import info.scce.pyro.core.command.HierarchyCommandExecuter;
import info.scce.pyro.core.command.FlowGraphCommandExecuter;
import info.scce.pyro.core.HierarchyController;
import info.scce.pyro.core.FlowGraphController;

@javax.transaction.Transactional
@javax.ws.rs.Path("/primerefs")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class PrimeRefsController {

	@javax.inject.Inject
	ProjectWebSocket projectWebSocket;
	
	@javax.inject.Inject
	GraphModelWebSocket graphModelWebSocket;
	
	@javax.inject.Inject
	GraphModelController graphModelController;

	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.inject.Inject
	info.scce.pyro.core.FileController fileController;
	
	@javax.inject.Inject
	HierarchyController primeGraphHierarchyController;
	
	@javax.inject.Inject
	FlowGraphController primeGraphFlowGraphController;

	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, CreateGraphModel graph) {

		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
        final entity.core.PyroFileContainerDB container = entity.core.PyroFileContainerDB.findById(graph.getparentId());
        
        if(container==null||subject==null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
        
        entity.core.PyroProjectDB pp = graphModelController.getProject(container);
		if (!canCreateGraphModel(subject, pp)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}

	    final entity.primerefs.PrimeRefsDB newGraph =  new entity.primerefs.PrimeRefsDB();
	    newGraph.filename = graph.getfilename();
        PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(subject,objectCache,graphModelWebSocket,newGraph, new java.util.LinkedList<>());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    newGraph.scale = 1.0;
	    newGraph.connector = "normal";
	    newGraph.height = 600L;
	    newGraph.width = 2000L;
	    newGraph.router = null;
	    newGraph.parent = container;
	    newGraph.isPublic = false;
	    
	    //primitive init
	    newGraph.extension = "pr";
        newGraph.persist();

       	if(container instanceof entity.core.PyroFolderDB) {
			((entity.core.PyroFolderDB) container).files_PrimeRefs.add(newGraph);
			container.persist();
		} else if(container instanceof entity.core.PyroProjectDB) {
			((entity.core.PyroProjectDB) container).files_PrimeRefs.add(newGraph);
		} else {
			throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
		}
		container.persist();
		
		projectWebSocket.send(pp.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(pp,objectCache)));
		
		return Response.ok(info.scce.pyro.primerefs.rest.PrimeRefs.fromEntity(newGraph,new info.scce.pyro.rest.ObjectCache())).build();

	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("read/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
												
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		entity.core.PyroProjectDB project = graphModelController.getProject(graph);
		if (!canReadGraphModel(subject, project)) {
        	return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		return Response.ok(info.scce.pyro.primerefs.rest.PrimeRefs.fromEntity(graph, objectCache))
				.build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("jumpto/{id}/{elementid}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id, @javax.ws.rs.PathParam("elementid") long elementId) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
												
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final PanacheEntity node = TypeRegistry.findById(elementId);
		
		if(node == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(subject,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
		
		entity.core.PyroProjectDB project = graphModelController.getProject(graph);
		if (!canReadGraphModel(subject, project)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		String graphModelType = null;
		String elementType = null;
		String primeGraphModelId = null;
		String primeElementlId = null;
		
		if(node instanceof entity.primerefs.PrimeToNodeDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToNode) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.SourceNode primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "PrimeRefs";
			elementType = "SourceNode";
		} else if(node instanceof entity.primerefs.PrimeToEdgeDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.SourceEdge primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "PrimeRefs";
			elementType = "SourceEdge";
		} else if(node instanceof entity.primerefs.PrimeToContainerDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.SourceContainer primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "PrimeRefs";
			elementType = "SourceContainer";
		} else if(node instanceof entity.primerefs.PrimeToGraphModelDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			primeGraphModelId = primeElementlId;
			graphModelType = "PrimeRefs";
			elementType = "PrimeRefs";
		} else if(node instanceof entity.primerefs.PrimeCToNodeDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.SourceNode primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "PrimeRefs";
			elementType = "SourceNode";
		} else if(node instanceof entity.primerefs.PrimeCToEdgeDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.SourceEdge primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "PrimeRefs";
			elementType = "SourceEdge";
		} else if(node instanceof entity.primerefs.PrimeCToContainerDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.SourceContainer primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "PrimeRefs";
			elementType = "SourceContainer";
		} else if(node instanceof entity.primerefs.PrimeCToGraphModelDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			primeGraphModelId = primeElementlId;
			graphModelType = "PrimeRefs";
			elementType = "PrimeRefs";
		} else if(node instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.hierarchy.hierarchy.D primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "Hierarchy";
			elementType = "D";
		} else if(node instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.hierarchy.hierarchy.C primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "Hierarchy";
			elementType = "C";
		} else if(node instanceof entity.primerefs.PrimeToNodeFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.Activity primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "FlowGraph";
			elementType = "Activity";
		} else if(node instanceof entity.primerefs.PrimeToEdgeFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "FlowGraph";
			elementType = "LabeledTransition";
		} else if(node instanceof entity.primerefs.PrimeToContainerFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "FlowGraph";
			elementType = "Swimlane";
		} else if(node instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraph primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			primeGraphModelId = primeElementlId;
			graphModelType = "FlowGraph";
			elementType = "FlowGraph";
		} else if(node instanceof entity.primerefs.PrimeCToNodeFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.Activity primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "FlowGraph";
			elementType = "Activity";
		} else if(node instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "FlowGraph";
			elementType = "LabeledTransition";
		} else if(node instanceof entity.primerefs.PrimeCToContainerFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			graphmodel.ModelElementContainer mec = primeNode.getRootElement();
			primeGraphModelId = mec.getId();
			graphModelType = "FlowGraph";
			elementType = "Swimlane";
		} else if(node instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
			final info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiNode = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) TypeRegistry.getDBToApi(node,executer);
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraph primeNode = apiNode.getPr();
			primeElementlId = primeNode.getId();
			primeGraphModelId = primeElementlId;
			graphModelType = "FlowGraph";
			elementType = "FlowGraph";
		}
		
		if(graphModelType == null || elementType == null)
			return Response.status(Response.Status.BAD_REQUEST).build();
		
		info.scce.pyro.message.JumpToPrimeAnswer resp = new info.scce.pyro.message.JumpToPrimeAnswer();
		resp.setGraphModelId(primeGraphModelId);
		resp.setElementId(primeElementlId);
		resp.setGraphModelType(graphModelType);
		resp.setElementType(elementType);
		
		return Response.ok(resp).build();
	}

	
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("customaction/{id}/{elementId}/fetch/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response fetchCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId) {
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		java.util.Map<String,String> map = new java.util.HashMap<>();
		
		return Response.ok(map).build();
	}
	
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("customaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		PrimeRefsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		return response;
	}
	

	@javax.ws.rs.POST
	@javax.ws.rs.Path("{id}/psaction/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerPostSelectActions(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		PrimeRefsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);

		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		return response;
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("dbaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerDoubleClickActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		PrimeRefsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		boolean hasExecuted = false;
		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		
		return response;
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("remove/{id}/{parentId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("parentId") final long parentId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		//find parent
		final entity.primerefs.PrimeRefsDB gm = entity.primerefs.PrimeRefsDB.findById(id);
		final entity.core.PyroFileContainerDB parent = entity.core.PyroFileContainerDB.findById(parentId);
		if(gm==null||parent==null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(gm);
		
		boolean succeeded = removeGraphModel(subject, pyroProject, parent, gm, false);
		if (!succeeded) {
			return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(subject,objectCache,graphModelWebSocket,gm,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		PrimeRefsFactory.eINSTANCE.warmup(pyroProject,projectWebSocket,subject,executer);
		removeContainer(new info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl(gm,executer));
		
		try {
			// trying to execute transaction (since deleting a graphmodel can lead to complex errors)
			gm.getEntityManager().flush();
		} catch(javax.persistence.PersistenceException e) {
			handleException(subject, pyroProject);
			System.out.println("ERROR: Could not delete GraphModel. This could be related to am existing reference, like e.g. prime-reference.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		// synchronize project-structure
		projectWebSocket.send(pyroProject.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(pyroProject,objectCache)));
		return Response.ok("OK").build();
	}
	
	/**
	 * This method creates a new transaction to send a correct update,
	 * after a failed transaction.
	 * @param subject
	 * @param pyroProject
	 */
	@javax.transaction.Transactional(javax.transaction.Transactional.TxType.NOT_SUPPORTED)
	public void handleException(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject) {
		// update project strucutre if e.g. prime-reference leads to incorrect deletion
		projectWebSocket.send(pyroProject.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(pyroProject,objectCache)));
	}
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject, entity.core.PyroFileContainerDB parent, entity.primerefs.PrimeRefsDB gm) {
		return removeGraphModel(subject, pyroProject, parent, gm, true);
	}
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject, entity.core.PyroFileContainerDB parent, entity.primerefs.PrimeRefsDB gm, boolean delete) {
		// check permission
		if (!canDeleteGraphModel(subject, pyroProject)) {
			return false;
        }
        
        // decouple from folder
        if(parent instanceof entity.core.PyroFolderDB) {
			entity.core.PyroFolderDB parentFolder = (entity.core.PyroFolderDB) parent;
			if(parentFolder.files_PrimeRefs.contains(gm)){
				parentFolder.files_PrimeRefs.remove(gm);
				parentFolder.persist();
			}
		} else if(parent instanceof entity.core.PyroProjectDB) {
			entity.core.PyroProjectDB project = (entity.core.PyroProjectDB) parent;
			if(project.files_PrimeRefs.contains(gm)){
				project.files_PrimeRefs.remove(gm);
				project.persist();
			}
		} else {
			return false;
		}
		
		// delete
		if(delete)
			gm.delete();
		
		return true;
	}
	
	private void removeContainer(info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		graph.delete();
	}

	@javax.ws.rs.POST
	@javax.ws.rs.Path("message/{graphModelId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response receiveMessage(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("graphModelId") long graphModelId, Message m) {
	    final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
	    final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(graphModelId);
	    if(subject==null||graph==null){
	        return Response.status(Response.Status.BAD_REQUEST).build();
	    }
	
	    entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(subject, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
	    }
	
	    if(m instanceof CompoundCommandMessage){
	        Response response = executeCommand((CompoundCommandMessage) m, subject, graph, securityContext);
			if(response.getStatus()==200){
				graphModelWebSocket.send(graphModelId,WebSocketMessage.fromEntity(subject.id,response.getEntity()));
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
	        graphModelWebSocket.send(graphModelId,WebSocketMessage.fromEntity(subject.id, m));
	        return Response.ok("OK").build();
	    }
	    else if (m instanceof PropertyMessage) {
			Response response = executePropertyUpdate((PropertyMessage) m, subject,graph);
			if(response.getStatus()==200){
				graphModelWebSocket.send(graphModelId,WebSocketMessage.fromEntity(subject.id,response.getEntity()));
			}
			return response;
		} else if (m instanceof ProjectMessage) {
			return Response.ok("OK").build();
		}
	
	    return Response.status(Response.Status.BAD_REQUEST).build();
	}

	private Response executePropertyUpdate(PropertyMessage pm,entity.core.PyroUserDB user, entity.primerefs.PrimeRefsDB graph) {
	    PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
	    info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    PrimeRefsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		
        
        String type = pm.getDelegate().get__type();
		if (type.equals("primerefs.SourceNode")){
			entity.primerefs.SourceNodeDB target = entity.primerefs.SourceNodeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.SourceNode targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.SourceNodeImpl(target,executer);
			executer.updateSourceNode(targetAPI, (info.scce.pyro.primerefs.rest.SourceNode) pm.getDelegate());
		} else 
		if (type.equals("primerefs.SourceContainer")){
			entity.primerefs.SourceContainerDB target = entity.primerefs.SourceContainerDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.SourceContainer targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.SourceContainerImpl(target,executer);
			executer.updateSourceContainer(targetAPI, (info.scce.pyro.primerefs.rest.SourceContainer) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToNode")){
			entity.primerefs.PrimeToNodeDB target = entity.primerefs.PrimeToNodeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToNode targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeImpl(target,executer);
			executer.updatePrimeToNode(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToNode) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToEdge")){
			entity.primerefs.PrimeToEdgeDB target = entity.primerefs.PrimeToEdgeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToEdge targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToEdgeImpl(target,executer);
			executer.updatePrimeToEdge(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToEdge) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToContainer")){
			entity.primerefs.PrimeToContainerDB target = entity.primerefs.PrimeToContainerDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToContainer targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToContainerImpl(target,executer);
			executer.updatePrimeToContainer(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToContainer) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToGraphModel")){
			entity.primerefs.PrimeToGraphModelDB target = entity.primerefs.PrimeToGraphModelDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToGraphModelImpl(target,executer);
			executer.updatePrimeToGraphModel(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToGraphModel) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToNode")){
			entity.primerefs.PrimeCToNodeDB target = entity.primerefs.PrimeCToNodeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNode targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToNodeImpl(target,executer);
			executer.updatePrimeCToNode(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToNode) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToEdge")){
			entity.primerefs.PrimeCToEdgeDB target = entity.primerefs.PrimeCToEdgeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToEdgeImpl(target,executer);
			executer.updatePrimeCToEdge(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToEdge) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToContainer")){
			entity.primerefs.PrimeCToContainerDB target = entity.primerefs.PrimeCToContainerDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToContainerImpl(target,executer);
			executer.updatePrimeCToContainer(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToContainer) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToGraphModel")){
			entity.primerefs.PrimeCToGraphModelDB target = entity.primerefs.PrimeCToGraphModelDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToGraphModelImpl(target,executer);
			executer.updatePrimeCToGraphModel(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToGraphModel) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToNodeHierarchy")){
			entity.primerefs.PrimeToNodeHierarchyDB target = entity.primerefs.PrimeToNodeHierarchyDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeHierarchyImpl(target,executer);
			executer.updatePrimeToNodeHierarchy(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToAbstractNodeHierarchy")){
			entity.primerefs.PrimeToAbstractNodeHierarchyDB target = entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToAbstractNodeHierarchyImpl(target,executer);
			executer.updatePrimeToAbstractNodeHierarchy(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToNodeFlow")){
			entity.primerefs.PrimeToNodeFlowDB target = entity.primerefs.PrimeToNodeFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeFlowImpl(target,executer);
			executer.updatePrimeToNodeFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToNodeFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToEdgeFlow")){
			entity.primerefs.PrimeToEdgeFlowDB target = entity.primerefs.PrimeToEdgeFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToEdgeFlowImpl(target,executer);
			executer.updatePrimeToEdgeFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToEdgeFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToContainerFlow")){
			entity.primerefs.PrimeToContainerFlowDB target = entity.primerefs.PrimeToContainerFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToContainerFlowImpl(target,executer);
			executer.updatePrimeToContainerFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToContainerFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeToGraphModelFlow")){
			entity.primerefs.PrimeToGraphModelFlowDB target = entity.primerefs.PrimeToGraphModelFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToGraphModelFlowImpl(target,executer);
			executer.updatePrimeToGraphModelFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToNodeFlow")){
			entity.primerefs.PrimeCToNodeFlowDB target = entity.primerefs.PrimeCToNodeFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToNodeFlowImpl(target,executer);
			executer.updatePrimeCToNodeFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToNodeFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToEdgeFlow")){
			entity.primerefs.PrimeCToEdgeFlowDB target = entity.primerefs.PrimeCToEdgeFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToEdgeFlowImpl(target,executer);
			executer.updatePrimeCToEdgeFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToContainerFlow")){
			entity.primerefs.PrimeCToContainerFlowDB target = entity.primerefs.PrimeCToContainerFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToContainerFlowImpl(target,executer);
			executer.updatePrimeCToContainerFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToContainerFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeCToGraphModelFlow")){
			entity.primerefs.PrimeCToGraphModelFlowDB target = entity.primerefs.PrimeCToGraphModelFlowDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToGraphModelFlowImpl(target,executer);
			executer.updatePrimeCToGraphModelFlow(targetAPI, (info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow) pm.getDelegate());
		} else 
		if (type.equals("primerefs.SourceEdge")){
			entity.primerefs.SourceEdgeDB target = entity.primerefs.SourceEdgeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.SourceEdge targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.SourceEdgeImpl(target,executer);
			executer.updateSourceEdge(targetAPI, (info.scce.pyro.primerefs.rest.SourceEdge) pm.getDelegate());
		} else 
		if (type.equals("primerefs.PrimeRefs")){
			entity.primerefs.PrimeRefsDB target = entity.primerefs.PrimeRefsDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs targetAPI = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl(target,executer);
			executer.updatePrimeRefs(targetAPI, (info.scce.pyro.primerefs.rest.PrimeRefs) pm.getDelegate());
		}
	    CompoundCommandMessage response = new CompoundCommandMessage();
		response.setType("basic_valid_answer");
		CompoundCommand cc = new CompoundCommand();
		cc.setQueue(executer.getBatch().getCommands());
		response.setCmd(cc);
		response.setGraphModelId(graph.id);
		response.setSenderId(user.id);
		response.setHighlightings(executer.getHighlightings());
		
		return Response.ok(response).build();
	}
	
	public PrimeRefsCommandExecuter buildExecuter(PanacheEntity ie,SecurityContext securityContext){
		PanacheEntity mec = ie;
		while (mec!=null) {
			if(mec instanceof entity.primerefs.PrimeRefsDB) {
				entity.primerefs.PrimeRefsDB container = (entity.primerefs.PrimeRefsDB) mec;
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				return new PrimeRefsCommandExecuter(user, objectCache, graphModelWebSocket, container, new java.util.LinkedList<>());
			} else if(mec instanceof entity.primerefs.SourceContainerDB) {
				entity.primerefs.SourceContainerDB container = (entity.primerefs.SourceContainerDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToNodeDB) {
				entity.primerefs.PrimeCToNodeDB container = (entity.primerefs.PrimeCToNodeDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToEdgeDB) {
				entity.primerefs.PrimeCToEdgeDB container = (entity.primerefs.PrimeCToEdgeDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToContainerDB) {
				entity.primerefs.PrimeCToContainerDB container = (entity.primerefs.PrimeCToContainerDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToGraphModelDB) {
				entity.primerefs.PrimeCToGraphModelDB container = (entity.primerefs.PrimeCToGraphModelDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToNodeFlowDB) {
				entity.primerefs.PrimeCToNodeFlowDB container = (entity.primerefs.PrimeCToNodeFlowDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
				entity.primerefs.PrimeCToEdgeFlowDB container = (entity.primerefs.PrimeCToEdgeFlowDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToContainerFlowDB) {
				entity.primerefs.PrimeCToContainerFlowDB container = (entity.primerefs.PrimeCToContainerFlowDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
				entity.primerefs.PrimeCToGraphModelFlowDB container = (entity.primerefs.PrimeCToGraphModelFlowDB) mec;
				mec = container.getContainer();
			}
			else {
				break;
			}
			
		}
		throw new IllegalStateException("Graphmodel could not be found");
	}
	
	private void createNode(String type,Object mec, long x, long y, Long primeId, PrimeRefsCommandExecuter executer,SecurityContext securityContext) {
		if(mec instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
			info.scce.cinco.product.primerefs.primerefs.SourceContainer n = (info.scce.cinco.product.primerefs.primerefs.SourceContainer) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNode n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow n = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs n = (info.scce.cinco.product.primerefs.primerefs.PrimeRefs) mec;
			if(type.equals("primerefs.SourceNode")) {
				n.newSourceNode(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.SourceContainer")) {
				n.newSourceContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNode")) {
				n.newPrimeToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdge")) {
				n.newPrimeToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainer")) {
				n.newPrimeToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModel")) {
				n.newPrimeToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNode")) {
				n.newPrimeCToNode(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdge")) {
				n.newPrimeCToEdge(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainer")) {
				n.newPrimeCToContainer(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModel")) {
				n.newPrimeCToGraphModel(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeHierarchy")) {
				n.newPrimeToNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToAbstractNodeHierarchy")) {
				n.newPrimeToAbstractNodeHierarchy(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToNodeFlow")) {
				n.newPrimeToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToEdgeFlow")) {
				n.newPrimeToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToContainerFlow")) {
				n.newPrimeToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeToGraphModelFlow")) {
				n.newPrimeToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToNodeFlow")) {
				n.newPrimeCToNodeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToEdgeFlow")) {
				n.newPrimeCToEdgeFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToContainerFlow")) {
				n.newPrimeCToContainerFlow(
				primeId,
				(int)x,
				(int)y);
			} else 
			if(type.equals("primerefs.PrimeCToGraphModelFlow")) {
				n.newPrimeCToGraphModelFlow(
				primeId,
				(int)x,
				(int)y);
			}
		}
	}
	
	private void addBendingPoints(PanacheEntity delegate, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions) {
		java.util.List<entity.core.BendingPointDB> bpEntities =  positions.stream().map(p -> {
		    entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
		    bp.x = p.getx();
		    bp.y = p.gety();
		    bp.persist();
		    return bp;
		}).collect(java.util.stream.Collectors.toList());
		
		if(delegate instanceof entity.primerefs.SourceEdgeDB) {
			entity.primerefs.SourceEdgeDB edge = (entity.primerefs.SourceEdgeDB) delegate;
			edge.bendingPoints.clear();
			edge.bendingPoints.addAll(bpEntities);
		}
		
		delegate.persist();
	}
	
	private graphmodel.Edge createEdge(String type, graphmodel.Node source, graphmodel.Node target, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions, PrimeRefsCommandExecuter executer) {
		graphmodel.Edge edge = null;
		
		
		return edge;
	}
	
    private Response executeCommand(CompoundCommandMessage ccm, entity.core.PyroUserDB user, entity.primerefs.PrimeRefsDB graph,SecurityContext securityContext) {
        //setup batch execution
        PrimeRefsCommandExecuter executer = new PrimeRefsCommandExecuter(user,objectCache,graphModelWebSocket,graph,ccm.getHighlightings());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
        PrimeRefsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
        
        //execute command
        try{
        	boolean isReOrUndo = ccm.getType().contains("redo")||ccm.getType().contains("undo");
	        for(Command c:ccm.getCmd().getQueue()){

	        	// CREATE NODE COMMAND
	            if(c instanceof CreateNodeCommand){
	                CreateNodeCommand cm = (CreateNodeCommand) c;
	                long containerId = resolveId(cm.getContainerId(), ccm.getRewriteRule());
	                
	                // resolving container
					graphmodel.ModelElementContainer cmec = (graphmodel.ModelElementContainer) TypeRegistry.findApiByType(cm.getContainerType(), containerId, executer);
					graphmodel.Node n = null;
					
					// creating node
					if(isReOrUndo) {
						if(cm.getDelegateId()!=0){
							if(c.getType().equals("primerefs.SourceNode")) {
								n = executer.createSourceNode(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.SourceNode) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.SourceContainer")) {
								n = executer.createSourceContainer(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.SourceContainer) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToNode")) {
								n = executer.createPrimeToNode(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToNode) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToEdge")) {
								n = executer.createPrimeToEdge(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToEdge) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToContainer")) {
								n = executer.createPrimeToContainer(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToContainer) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToGraphModel")) {
								n = executer.createPrimeToGraphModel(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToGraphModel) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToNode")) {
								n = executer.createPrimeCToNode(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToNode) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToEdge")) {
								n = executer.createPrimeCToEdge(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToEdge) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToContainer")) {
								n = executer.createPrimeCToContainer(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToContainer) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToGraphModel")) {
								n = executer.createPrimeCToGraphModel(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToGraphModel) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToNodeHierarchy")) {
								n = executer.createPrimeToNodeHierarchy(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToAbstractNodeHierarchy")) {
								n = executer.createPrimeToAbstractNodeHierarchy(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToNodeFlow")) {
								n = executer.createPrimeToNodeFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToNodeFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToEdgeFlow")) {
								n = executer.createPrimeToEdgeFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToEdgeFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToContainerFlow")) {
								n = executer.createPrimeToContainerFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToContainerFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeToGraphModelFlow")) {
								n = executer.createPrimeToGraphModelFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToNodeFlow")) {
								n = executer.createPrimeCToNodeFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToNodeFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToEdgeFlow")) {
								n = executer.createPrimeCToEdgeFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToContainerFlow")) {
								n = executer.createPrimeCToContainerFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToContainerFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("primerefs.PrimeCToGraphModelFlow")) {
								n = executer.createPrimeCToGraphModelFlow(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							}
						} else {
							if(c.getType().equals("primerefs.SourceNode")) {
								executer.createSourceNode(
									cm.getX(),
									cm.getY(),
									36,
									36,
									cmec,
									(info.scce.pyro.primerefs.rest.SourceNode)cm.getElement());
							} else if(c.getType().equals("primerefs.SourceContainer")) {
								executer.createSourceContainer(
									cm.getX(),
									cm.getY(),
									36,
									36,
									cmec,
									(info.scce.pyro.primerefs.rest.SourceContainer)cm.getElement());
							} else if(c.getType().equals("primerefs.PrimeToNode")) {
								executer.createPrimeToNode(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToNode)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToEdge")) {
								executer.createPrimeToEdge(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToEdge)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToContainer")) {
								executer.createPrimeToContainer(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToContainer)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToGraphModel")) {
								executer.createPrimeToGraphModel(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToGraphModel)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToNode")) {
								executer.createPrimeCToNode(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToNode)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToEdge")) {
								executer.createPrimeCToEdge(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToEdge)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToContainer")) {
								executer.createPrimeCToContainer(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToContainer)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToGraphModel")) {
								executer.createPrimeCToGraphModel(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToGraphModel)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToNodeHierarchy")) {
								executer.createPrimeToNodeHierarchy(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToAbstractNodeHierarchy")) {
								executer.createPrimeToAbstractNodeHierarchy(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToNodeFlow")) {
								executer.createPrimeToNodeFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToNodeFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToEdgeFlow")) {
								executer.createPrimeToEdgeFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToEdgeFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToContainerFlow")) {
								executer.createPrimeToContainerFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToContainerFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeToGraphModelFlow")) {
								executer.createPrimeToGraphModelFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToNodeFlow")) {
								executer.createPrimeCToNodeFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToNodeFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToEdgeFlow")) {
								executer.createPrimeCToEdgeFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToContainerFlow")) {
								executer.createPrimeCToContainerFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToContainerFlow)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("primerefs.PrimeCToGraphModelFlow")) {
								executer.createPrimeCToGraphModelFlow(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow)cm.getElement(),
									cm.getPrimeId()
									);
							}
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
					graphmodel.ModelElementContainer cmec = (graphmodel.ModelElementContainer) TypeRegistry.findApiByType(cm.getContainerType(), containerId, executer);
					graphmodel.Node cn = (graphmodel.Node) TypeRegistry.findApiByType(c.getType(), delegateId, executer);
					
					// move node
					cn.moveTo(cmec,java.lang.Math.toIntExact(cm.getX()), java.lang.Math.toIntExact(cm.getY()));
				}
				
				// RESIZE NODE COMMAND
				else if(c instanceof ResizeNodeCommand){
					ResizeNodeCommand cm = (ResizeNodeCommand) c;
					long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
					
					// resolving elements
					graphmodel.Node cn = (graphmodel.Node) TypeRegistry.findApiByType(c.getType(), delegateId, executer);
					
					cn.resize(java.lang.Math.toIntExact(cm.getWidth()), java.lang.Math.toIntExact(cm.getHeight()));
				}
				
				// REMOVE NODE COMMAND
				else if(c instanceof RemoveNodeCommand){
					RemoveNodeCommand cm = (RemoveNodeCommand) c;
					long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
					
					// resolving elements
					graphmodel.Node cn = (graphmodel.Node) TypeRegistry.findApiByType(c.getType(), delegateId, executer);
					
					// delete node
					cn.delete();
				}

	            // CREATE EDGE COMMAND
	            else if(c instanceof CreateEdgeCommand){
	                CreateEdgeCommand cm = (CreateEdgeCommand) c;
	                long sourceId = resolveId(cm.getSourceId(), ccm.getRewriteRule());
	                long targetId = resolveId(cm.getTargetId(), ccm.getRewriteRule());

					// resolving elements
					graphmodel.Node source = (graphmodel.Node) TypeRegistry.findApiByType(cm.getSourceType(), sourceId, executer);
					graphmodel.Node target = (graphmodel.Node) TypeRegistry.findApiByType(cm.getTargetType(), targetId, executer);
					
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
					graphmodel.Node source = (graphmodel.Node) TypeRegistry.findApiByType(cm.getSourceType(), sourceId, executer);
					graphmodel.Node target = (graphmodel.Node) TypeRegistry.findApiByType(cm.getTargetType(), targetId, executer);
					graphmodel.Edge ce = (graphmodel.Edge) TypeRegistry.findApiByType(c.getType(), delegateId, executer);
					
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
	                graphmodel.Edge ce = (graphmodel.Edge) TypeRegistry.findApiByType(c.getType(), delegateId, executer);
	               	
	               	// delete edge
                	ce.delete();
	            }
				
				// UPDATE BEND POINT COMMAND
				else if(c instanceof UpdateBendPointCommand){
					UpdateBendPointCommand cm = (UpdateBendPointCommand) c;
					long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
					
					// resolving elements
					graphmodel.Edge ce = (graphmodel.Edge) TypeRegistry.findApiByType(c.getType(), delegateId, executer);
					
					// updated edge
					if(ce instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
						info.scce.cinco.product.primerefs.primerefs.SourceEdge apiEdge = (info.scce.cinco.product.primerefs.primerefs.SourceEdge) ce;
						executer.updateSourceEdge(apiEdge, cm.getPositions());
					}
					
					// persist update
					PanacheEntity edge = TypeRegistry.getApiToDB(ce); 
					edge.persist();
	            }

	            // UPDATE COMMAND (RE OR UNDO)
	            else if(c instanceof UpdateCommand && isReOrUndo){
	            	UpdateCommand cm = (UpdateCommand) c;
	                long delegateId = resolveId(cm.getDelegateId(), ccm.getRewriteRule());
					
					// resolving elements
					graphmodel.IdentifiableElement ce = TypeRegistry.findApiByType(c.getType(), delegateId, executer);
					
					// update element
					if(ce instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
						executer.updateSourceNode(
							(info.scce.cinco.product.primerefs.primerefs.SourceNode) ce,
							(info.scce.pyro.primerefs.rest.SourceNode) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
						executer.updateSourceContainer(
							(info.scce.cinco.product.primerefs.primerefs.SourceContainer) ce,
							(info.scce.pyro.primerefs.rest.SourceContainer) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
						executer.updatePrimeToNode(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToNode) ce,
							(info.scce.pyro.primerefs.rest.PrimeToNode) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
						executer.updatePrimeToEdge(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) ce,
							(info.scce.pyro.primerefs.rest.PrimeToEdge) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
						executer.updatePrimeToContainer(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) ce,
							(info.scce.pyro.primerefs.rest.PrimeToContainer) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
						executer.updatePrimeToGraphModel(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) ce,
							(info.scce.pyro.primerefs.rest.PrimeToGraphModel) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
						executer.updatePrimeCToNode(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToNode) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
						executer.updatePrimeCToEdge(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToEdge) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
						executer.updatePrimeCToContainer(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToContainer) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
						executer.updatePrimeCToGraphModel(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToGraphModel) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
						executer.updatePrimeToNodeHierarchy(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) ce,
							(info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
						executer.updatePrimeToAbstractNodeHierarchy(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) ce,
							(info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
						executer.updatePrimeToNodeFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeToNodeFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
						executer.updatePrimeToEdgeFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeToEdgeFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
						executer.updatePrimeToContainerFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeToContainerFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
						executer.updatePrimeToGraphModelFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
						executer.updatePrimeCToNodeFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToNodeFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
						executer.updatePrimeCToEdgeFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
						executer.updatePrimeCToContainerFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToContainerFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
						executer.updatePrimeCToGraphModelFlow(
							(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) ce,
							(info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
						executer.updateSourceEdge(
							(info.scce.cinco.product.primerefs.primerefs.SourceEdge) ce,
							(info.scce.pyro.primerefs.rest.SourceEdge) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
						executer.updatePrimeRefs(
							(info.scce.cinco.product.primerefs.primerefs.PrimeRefs) ce,
							(info.scce.pyro.primerefs.rest.PrimeRefs) cm.getElement()
						);
					}
				}
	            else {
					return Response.status(Response.Status.BAD_REQUEST).build();
	            }
	        }
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
    
    private Response createResponse(String type,PrimeRefsCommandExecuter executer,long userId,long graphId,java.util.List<RewriteRule> rewriteRuleList) {
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
    
    private boolean canCreateGraphModel(
    	entity.core.PyroUserDB user,
    	entity.core.PyroProjectDB project) {
    	return canPerformOperation(user, project, entity.core.PyroCrudOperationDB.CREATE);
    }

	private boolean canReadGraphModel(
    	entity.core.PyroUserDB user,
    	entity.core.PyroProjectDB project) {
    	return canPerformOperation(user, project, entity.core.PyroCrudOperationDB.READ);
    }
    
    private boolean canUpdateGraphModel(
    	entity.core.PyroUserDB user,
    	entity.core.PyroProjectDB project) {
    	return canPerformOperation(user, project, entity.core.PyroCrudOperationDB.UPDATE);
    }
    	    
    private boolean canDeleteGraphModel(
    	entity.core.PyroUserDB user,
    	entity.core.PyroProjectDB project) {
    	return canPerformOperation(user, project, entity.core.PyroCrudOperationDB.DELETE);
    }
    
    private boolean canPerformOperation(
    	entity.core.PyroUserDB user,
    	entity.core.PyroProjectDB project,
    	entity.core.PyroCrudOperationDB operation	    	
    ) {
		final java.util.List<entity.core.PyroGraphModelPermissionVectorDB> result = entity.core.PyroGraphModelPermissionVectorDB
		.list("user = ?1 and project = ?2 and graphModelType = ?3",user,project,entity.core.PyroGraphModelTypeDB.PRIME_REFS);
		return result.size() < 1 ? false : result.get(0).permissions.contains(operation);
    }
}

