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

import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;
import info.scce.cinco.product.ha.hooksandactions.HooksAndActionsFactory;


@javax.transaction.Transactional
@javax.ws.rs.Path("/hooksandactions")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class HooksAndActionsController {

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

	    final entity.hooksandactions.HooksAndActionsDB newGraph =  new entity.hooksandactions.HooksAndActionsDB();
	    newGraph.filename = graph.getfilename();
        HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(subject,objectCache,graphModelWebSocket,newGraph, new java.util.LinkedList<>());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    newGraph.scale = 1.0;
	    newGraph.connector = "normal";
	    newGraph.height = 600L;
	    newGraph.width = 2000L;
	    newGraph.router = null;
	    newGraph.parent = container;
	    newGraph.isPublic = false;
	    
	    //primitive init
	    newGraph.attribute = null;
	    newGraph.extension = "ha";
        newGraph.persist();

       	if(container instanceof entity.core.PyroFolderDB) {
			((entity.core.PyroFolderDB) container).files_HooksAndActions.add(newGraph);
			container.persist();
		} else if(container instanceof entity.core.PyroProjectDB) {
			((entity.core.PyroProjectDB) container).files_HooksAndActions.add(newGraph);
		} else {
			throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
		}
		container.persist();
		
		projectWebSocket.send(pp.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(pp,objectCache)));
		
		HooksAndActionsFactory.eINSTANCE.warmup(pp,projectWebSocket,subject,executer);
		info.scce.cinco.product.ha.hooksandactions.HooksAndActions ce = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(newGraph,executer);
		info.scce.cinco.product.flowgraph.hooks.PostCreate ca = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
		ca.init(executer);
		info.scce.cinco.product.ha.hooksandactions.HooksAndActions newGraphApi = (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) TypeRegistry.getDBToApi(newGraph, executer);
		ca.postCreate(newGraphApi);
		return Response.ok(info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntity(newGraph,new info.scce.pyro.rest.ObjectCache())).build();

	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("read/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
												
		final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		entity.core.PyroProjectDB project = graphModelController.getProject(graph);
		if (!canReadGraphModel(subject, project)) {
        	return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		return Response.ok(info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntity(graph, objectCache))
				.build();
	}

	
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("customaction/{id}/{elementId}/fetch/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response fetchCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId) {
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		java.util.Map<String,String> map = new java.util.HashMap<>();
		
		HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HooksAndActionsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		PanacheEntity e = TypeRegistry.findById(elementId);
		
		if(e instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB dbEntity = (entity.hooksandactions.HookAContainerDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookAContainer ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(dbEntity,executer);
			// customAction 0
			info.scce.cinco.product.flowgraph.action.ContextMenuAction ca0 = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				map.put("info.scce.cinco.product.flowgraph.action.ContextMenuAction",ca0.getName());
			}
		} else if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB dbEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(dbEntity,executer);
			// customAction 0
			info.scce.cinco.product.flowgraph.action.ContextMenuAction ca0 = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				map.put("info.scce.cinco.product.flowgraph.action.ContextMenuAction",ca0.getName());
			}
		} else if(e instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB dbEntity = (entity.hooksandactions.HookANodeDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookANode ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(dbEntity,executer);
			// customAction 0
			info.scce.cinco.product.flowgraph.action.ContextMenuAction2 ca0 = new info.scce.cinco.product.flowgraph.action.ContextMenuAction2();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				map.put("info.scce.cinco.product.flowgraph.action.ContextMenuAction2",ca0.getName());
			}
			// customAction 1
			info.scce.cinco.product.flowgraph.action.ContextMenuAction ca1 = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
			ca1.init(executer);
			if(ca1.canExecute(ce)){
				map.put("info.scce.cinco.product.flowgraph.action.ContextMenuAction",ca1.getName());
			}
		} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB dbEntity = (entity.hooksandactions.HooksAndActionsDB) e;
			info.scce.cinco.product.ha.hooksandactions.HooksAndActions ce = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(dbEntity,executer);
			// customAction 0
			info.scce.cinco.product.flowgraph.action.ContextMenuAction ca0 = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				map.put("info.scce.cinco.product.flowgraph.action.ContextMenuAction",ca0.getName());
			}
		}
		
		return Response.ok(map).build();
	}
	
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("customaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HooksAndActionsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		
		PanacheEntity e = TypeRegistry.findById(elementId);
		
		if(e instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB dbEntity = (entity.hooksandactions.HookAContainerDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookAContainer ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(dbEntity,executer);
			if(action.getFqn().equals("info.scce.cinco.product.flowgraph.action.ContextMenuAction")) {
				info.scce.cinco.product.flowgraph.action.ContextMenuAction ca = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
				ca.init(executer);
				ca.execute(ce);
			}
		} else if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB dbEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(dbEntity,executer);
			if(action.getFqn().equals("info.scce.cinco.product.flowgraph.action.ContextMenuAction")) {
				info.scce.cinco.product.flowgraph.action.ContextMenuAction ca = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
				ca.init(executer);
				ca.execute(ce);
			}
		} else if(e instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB dbEntity = (entity.hooksandactions.HookANodeDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookANode ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(dbEntity,executer);
			if(action.getFqn().equals("info.scce.cinco.product.flowgraph.action.ContextMenuAction2")) {
				info.scce.cinco.product.flowgraph.action.ContextMenuAction2 ca = new info.scce.cinco.product.flowgraph.action.ContextMenuAction2();
				ca.init(executer);
				ca.execute(ce);
			} else if(action.getFqn().equals("info.scce.cinco.product.flowgraph.action.ContextMenuAction")) {
				info.scce.cinco.product.flowgraph.action.ContextMenuAction ca = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
				ca.init(executer);
				ca.execute(ce);
			}
		} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB dbEntity = (entity.hooksandactions.HooksAndActionsDB) e;
			info.scce.cinco.product.ha.hooksandactions.HooksAndActions ce = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(dbEntity,executer);
			if(action.getFqn().equals("info.scce.cinco.product.flowgraph.action.ContextMenuAction")) {
				info.scce.cinco.product.flowgraph.action.ContextMenuAction ca = new info.scce.cinco.product.flowgraph.action.ContextMenuAction();
				ca.init(executer);
				ca.execute(ce);
			}
		}
		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		return response;
	}
	

	@javax.ws.rs.POST
	@javax.ws.rs.Path("{id}/psaction/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerPostSelectActions(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		
		final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HooksAndActionsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);

		
		boolean hasExecuted = false;
		String typeName = action.getFqn();
		if("hooksandactions.HookAContainer".equals(typeName)) {
			PanacheEntity elem = TypeRegistry.findByType(typeName, elementId);
			entity.hooksandactions.HookAContainerDB e = (entity.hooksandactions.HookAContainerDB)elem;
			info.scce.cinco.product.ha.hooksandactions.HookAContainer ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(e,executer);
			{
				info.scce.cinco.product.flowgraph.hooks.PostSelect ca = new info.scce.cinco.product.flowgraph.hooks.PostSelect();
				ca.init(executer);
				ca.postSelect(ce);
			}
		} else if("hooksandactions.HookAnEdge".equals(typeName)) {
			PanacheEntity elem = TypeRegistry.findByType(typeName, elementId);
			entity.hooksandactions.HookAnEdgeDB e = (entity.hooksandactions.HookAnEdgeDB)elem;
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(e,executer);
			{
				info.scce.cinco.product.flowgraph.hooks.PostSelect ca = new info.scce.cinco.product.flowgraph.hooks.PostSelect();
				ca.init(executer);
				ca.postSelect(ce);
			}
		} else if("hooksandactions.HookANode".equals(typeName)) {
			PanacheEntity elem = TypeRegistry.findByType(typeName, elementId);
			entity.hooksandactions.HookANodeDB e = (entity.hooksandactions.HookANodeDB)elem;
			info.scce.cinco.product.ha.hooksandactions.HookANode ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(e,executer);
			{
				info.scce.cinco.product.flowgraph.hooks.PostSelect ca = new info.scce.cinco.product.flowgraph.hooks.PostSelect();
				ca.init(executer);
				ca.postSelect(ce);
			}
		}
		
		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		//propagate
		graphModelWebSocket.send(id,WebSocketMessage.fromEntity(user.id,response.getEntity()));
		return response;
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("dbaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerDoubleClickActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HooksAndActionsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		boolean hasExecuted = false;
		
		PanacheEntity e = TypeRegistry.findById(elementId);
		
		if(e instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB dbEntity = (entity.hooksandactions.HookAContainerDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookAContainer ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(dbEntity,executer);
			// doubleClickAction 0
			info.scce.cinco.product.flowgraph.action.DoubleClickAction ca0 = new info.scce.cinco.product.flowgraph.action.DoubleClickAction();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				ca0.execute(ce);
				hasExecuted = true;
			}
		} else if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB dbEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(dbEntity,executer);
			// doubleClickAction 0
			info.scce.cinco.product.flowgraph.action.DoubleClickAction ca0 = new info.scce.cinco.product.flowgraph.action.DoubleClickAction();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				ca0.execute(ce);
				hasExecuted = true;
			}
		} else if(e instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB dbEntity = (entity.hooksandactions.HookANodeDB) e;
			info.scce.cinco.product.ha.hooksandactions.HookANode ce = new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(dbEntity,executer);
			// doubleClickAction 0
			info.scce.cinco.product.flowgraph.action.DoubleClickAction2 ca0 = new info.scce.cinco.product.flowgraph.action.DoubleClickAction2();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				ca0.execute(ce);
				hasExecuted = true;
			}
			// doubleClickAction 1
			info.scce.cinco.product.flowgraph.action.DoubleClickAction ca1 = new info.scce.cinco.product.flowgraph.action.DoubleClickAction();
			ca1.init(executer);
			if(ca1.canExecute(ce)){
				ca1.execute(ce);
				hasExecuted = true;
			}
		} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB dbEntity = (entity.hooksandactions.HooksAndActionsDB) e;
			info.scce.cinco.product.ha.hooksandactions.HooksAndActions ce = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(dbEntity,executer);
			// doubleClickAction 0
			info.scce.cinco.product.flowgraph.action.DoubleClickAction ca0 = new info.scce.cinco.product.flowgraph.action.DoubleClickAction();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				ca0.execute(ce);
				hasExecuted = true;
			}
		}
		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		if(hasExecuted){
			//propagate
			graphModelWebSocket.send(id,WebSocketMessage.fromEntity(user.id,response.getEntity()));
		}
		
		return response;
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("remove/{id}/{parentId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("parentId") final long parentId) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		//find parent
		final entity.hooksandactions.HooksAndActionsDB gm = entity.hooksandactions.HooksAndActionsDB.findById(id);
		final entity.core.PyroFileContainerDB parent = entity.core.PyroFileContainerDB.findById(parentId);
		if(gm==null||parent==null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(gm);
		
		boolean succeeded = removeGraphModel(subject, pyroProject, parent, gm, false);
		if (!succeeded) {
			return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(subject,objectCache,graphModelWebSocket,gm,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HooksAndActionsFactory.eINSTANCE.warmup(pyroProject,projectWebSocket,subject,executer);
		removeContainer(new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(gm,executer));
		
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
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject, entity.core.PyroFileContainerDB parent, entity.hooksandactions.HooksAndActionsDB gm) {
		return removeGraphModel(subject, pyroProject, parent, gm, true);
	}
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject, entity.core.PyroFileContainerDB parent, entity.hooksandactions.HooksAndActionsDB gm, boolean delete) {
		// check permission
		if (!canDeleteGraphModel(subject, pyroProject)) {
			return false;
        }
        
        // decouple from folder
        if(parent instanceof entity.core.PyroFolderDB) {
			entity.core.PyroFolderDB parentFolder = (entity.core.PyroFolderDB) parent;
			if(parentFolder.files_HooksAndActions.contains(gm)){
				parentFolder.files_HooksAndActions.remove(gm);
				parentFolder.persist();
			}
		} else if(parent instanceof entity.core.PyroProjectDB) {
			entity.core.PyroProjectDB project = (entity.core.PyroProjectDB) parent;
			if(project.files_HooksAndActions.contains(gm)){
				project.files_HooksAndActions.remove(gm);
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
	
	private void removeContainer(info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {
		graph.delete();
	}

	@javax.ws.rs.POST
	@javax.ws.rs.Path("message/{graphModelId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response receiveMessage(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("graphModelId") long graphModelId, Message m) {
	    final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
	    final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(graphModelId);
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

	private Response executePropertyUpdate(PropertyMessage pm,entity.core.PyroUserDB user, entity.hooksandactions.HooksAndActionsDB graph) {
	    HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
	    info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    HooksAndActionsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		
        
        String type = pm.getDelegate().get__type();
		if (type.equals("hooksandactions.HookAContainer")){
			entity.hooksandactions.HookAContainerDB target = entity.hooksandactions.HookAContainerDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.ha.hooksandactions.HookAContainer targetAPI = new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(target,executer);
			executer.updateHookAContainer(targetAPI, (info.scce.pyro.hooksandactions.rest.HookAContainer) pm.getDelegate());
		} else 
		if (type.equals("hooksandactions.HookAnEdge")){
			entity.hooksandactions.HookAnEdgeDB target = entity.hooksandactions.HookAnEdgeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge targetAPI = new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(target,executer);
			executer.updateHookAnEdge(targetAPI, (info.scce.pyro.hooksandactions.rest.HookAnEdge) pm.getDelegate());
		} else 
		if (type.equals("hooksandactions.HookANode")){
			entity.hooksandactions.HookANodeDB target = entity.hooksandactions.HookANodeDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.ha.hooksandactions.HookANode targetAPI = new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(target,executer);
			executer.updateHookANode(targetAPI, (info.scce.pyro.hooksandactions.rest.HookANode) pm.getDelegate());
		} else 
		if (type.equals("hooksandactions.HooksAndActions")){
			entity.hooksandactions.HooksAndActionsDB target = entity.hooksandactions.HooksAndActionsDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.ha.hooksandactions.HooksAndActions targetAPI = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(target,executer);
			executer.updateHooksAndActions(targetAPI, (info.scce.pyro.hooksandactions.rest.HooksAndActions) pm.getDelegate());
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
	
	public HooksAndActionsCommandExecuter buildExecuter(PanacheEntity ie,SecurityContext securityContext){
		PanacheEntity mec = ie;
		while (mec!=null) {
			if(mec instanceof entity.hooksandactions.HooksAndActionsDB) {
				entity.hooksandactions.HooksAndActionsDB container = (entity.hooksandactions.HooksAndActionsDB) mec;
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				return new HooksAndActionsCommandExecuter(user, objectCache, graphModelWebSocket, container, new java.util.LinkedList<>());
			} else if(mec instanceof entity.hooksandactions.HookAContainerDB) {
				entity.hooksandactions.HookAContainerDB container = (entity.hooksandactions.HookAContainerDB) mec;
				mec = container.getContainer();
			}
			else {
				break;
			}
			
		}
		throw new IllegalStateException("Graphmodel could not be found");
	}
	
	private void createNode(String type,Object mec, long x, long y, Long primeId, HooksAndActionsCommandExecuter executer,SecurityContext securityContext) {
		if(mec instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
			info.scce.cinco.product.ha.hooksandactions.HookAContainer n = (info.scce.cinco.product.ha.hooksandactions.HookAContainer) mec;
			if(type.equals("hooksandactions.HookAContainer")) {
				n.newHookAContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("hooksandactions.HookANode")) {
				n.newHookANode(
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions) {
			info.scce.cinco.product.ha.hooksandactions.HooksAndActions n = (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) mec;
			if(type.equals("hooksandactions.HookAContainer")) {
				n.newHookAContainer(
				(int)x,
				(int)y);
			} else 
			if(type.equals("hooksandactions.HookANode")) {
				n.newHookANode(
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
		
		if(delegate instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB edge = (entity.hooksandactions.HookAnEdgeDB) delegate;
			edge.bendingPoints.clear();
			edge.bendingPoints.addAll(bpEntities);
		}
		
		delegate.persist();
	}
	
	private graphmodel.Edge createEdge(String type, graphmodel.Node source, graphmodel.Node target, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions, HooksAndActionsCommandExecuter executer) {
		graphmodel.Edge edge = null;
		
		if(source instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
			if(type.equals("hooksandactions.HookAnEdge")) {
				if(
					target instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer
					|| target instanceof info.scce.cinco.product.ha.hooksandactions.HookANode
				) {
					edge = executer.createHookAnEdge(source, target, positions, null);
				}
			}
		} else if(source instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
			if(type.equals("hooksandactions.HookAnEdge")) {
				if(
					target instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer
					|| target instanceof info.scce.cinco.product.ha.hooksandactions.HookANode
				) {
					edge = executer.createHookAnEdge(source, target, positions, null);
				}
			}
		}
		
		return edge;
	}
	
    private Response executeCommand(CompoundCommandMessage ccm, entity.core.PyroUserDB user, entity.hooksandactions.HooksAndActionsDB graph,SecurityContext securityContext) {
        //setup batch execution
        HooksAndActionsCommandExecuter executer = new HooksAndActionsCommandExecuter(user,objectCache,graphModelWebSocket,graph,ccm.getHighlightings());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
        HooksAndActionsFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
        
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
							if(c.getType().equals("hooksandactions.HookAContainer")) {
								n = executer.createHookAContainer(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hooksandactions.rest.HookAContainer) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("hooksandactions.HookANode")) {
								n = executer.createHookANode(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hooksandactions.rest.HookANode) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							}
						} else {
							if(c.getType().equals("hooksandactions.HookAContainer")) {
								executer.createHookAContainer(
									cm.getX(),
									cm.getY(),
									36,
									36,
									cmec,
									(info.scce.pyro.hooksandactions.rest.HookAContainer)cm.getElement());
							} else if(c.getType().equals("hooksandactions.HookANode")) {
								executer.createHookANode(
									cm.getX(),
									cm.getY(),
									36,
									36,
									cmec,
									(info.scce.pyro.hooksandactions.rest.HookANode)cm.getElement());
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
					if(ce instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
						info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiEdge = (info.scce.cinco.product.ha.hooksandactions.HookAnEdge) ce;
						executer.updateHookAnEdge(apiEdge, cm.getPositions());
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
					if(ce instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
						executer.updateHookAContainer(
							(info.scce.cinco.product.ha.hooksandactions.HookAContainer) ce,
							(info.scce.pyro.hooksandactions.rest.HookAContainer) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
						executer.updateHookAnEdge(
							(info.scce.cinco.product.ha.hooksandactions.HookAnEdge) ce,
							(info.scce.pyro.hooksandactions.rest.HookAnEdge) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
						executer.updateHookANode(
							(info.scce.cinco.product.ha.hooksandactions.HookANode) ce,
							(info.scce.pyro.hooksandactions.rest.HookANode) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions) {
						executer.updateHooksAndActions(
							(info.scce.cinco.product.ha.hooksandactions.HooksAndActions) ce,
							(info.scce.pyro.hooksandactions.rest.HooksAndActions) cm.getElement()
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
    
    private Response createResponse(String type,HooksAndActionsCommandExecuter executer,long userId,long graphId,java.util.List<RewriteRule> rewriteRuleList) {
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
		.list("user = ?1 and project = ?2 and graphModelType = ?3",user,project,entity.core.PyroGraphModelTypeDB.HOOKS_AND_ACTIONS);
		return result.size() < 1 ? false : result.get(0).permissions.contains(operation);
    }
}

