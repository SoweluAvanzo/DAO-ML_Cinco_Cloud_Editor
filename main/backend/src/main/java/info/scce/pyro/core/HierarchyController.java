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

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import info.scce.pyro.core.command.HierarchyCommandExecuter;
import info.scce.cinco.product.hierarchy.hierarchy.HierarchyFactory;


@javax.transaction.Transactional
@javax.ws.rs.Path("/hierarchy")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class HierarchyController {

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

	    final entity.hierarchy.HierarchyDB newGraph =  new entity.hierarchy.HierarchyDB();
	    newGraph.filename = graph.getfilename();
        HierarchyCommandExecuter executer = new HierarchyCommandExecuter(subject,objectCache,graphModelWebSocket,newGraph, new java.util.LinkedList<>());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    newGraph.scale = 1.0;
	    newGraph.connector = "normal";
	    newGraph.height = 600L;
	    newGraph.width = 2000L;
	    newGraph.router = null;
	    newGraph.parent = container;
	    newGraph.isPublic = false;
	    
	    //primitive init
	    newGraph.modelName = null;
	    newGraph.extension = "hierarchy";
        newGraph.persist();

       	if(container instanceof entity.core.PyroFolderDB) {
			((entity.core.PyroFolderDB) container).files_Hierarchy.add(newGraph);
			container.persist();
		} else if(container instanceof entity.core.PyroProjectDB) {
			((entity.core.PyroProjectDB) container).files_Hierarchy.add(newGraph);
		} else {
			throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
		}
		container.persist();
		
		projectWebSocket.send(pp.id, WebSocketMessage.fromEntity(subject.id, info.scce.pyro.core.rest.types.PyroProjectStructure.fromEntity(pp,objectCache)));
		
		return Response.ok(info.scce.pyro.hierarchy.rest.Hierarchy.fromEntity(newGraph,new info.scce.pyro.rest.ObjectCache())).build();

	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("read/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
												
		final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		entity.core.PyroProjectDB project = graphModelController.getProject(graph);
		if (!canReadGraphModel(subject, project)) {
        	return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		return Response.ok(info.scce.pyro.hierarchy.rest.Hierarchy.fromEntity(graph, objectCache))
				.build();
	}

	
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("customaction/{id}/{elementId}/fetch/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response fetchCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId) {
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(id);
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
		final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		HierarchyCommandExecuter executer = new HierarchyCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HierarchyFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		return response;
	}
	

	@javax.ws.rs.POST
	@javax.ws.rs.Path("{id}/psaction/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerPostSelectActions(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		
		final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		HierarchyCommandExecuter executer = new HierarchyCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HierarchyFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);

		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		return response;
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("dbaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerDoubleClickActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(graph);
		if (!canUpdateGraphModel(user, pyroProject)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		HierarchyCommandExecuter executer = new HierarchyCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HierarchyFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
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
		final entity.hierarchy.HierarchyDB gm = entity.hierarchy.HierarchyDB.findById(id);
		final entity.core.PyroFileContainerDB parent = entity.core.PyroFileContainerDB.findById(parentId);
		if(gm==null||parent==null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		entity.core.PyroProjectDB pyroProject = graphModelController.getProject(gm);
		
		boolean succeeded = removeGraphModel(subject, pyroProject, parent, gm, false);
		if (!succeeded) {
			return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		HierarchyCommandExecuter executer = new HierarchyCommandExecuter(subject,objectCache,graphModelWebSocket,gm,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		HierarchyFactory.eINSTANCE.warmup(pyroProject,projectWebSocket,subject,executer);
		removeContainer(new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(gm,executer));
		
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
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject, entity.core.PyroFileContainerDB parent, entity.hierarchy.HierarchyDB gm) {
		return removeGraphModel(subject, pyroProject, parent, gm, true);
	}
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.core.PyroProjectDB pyroProject, entity.core.PyroFileContainerDB parent, entity.hierarchy.HierarchyDB gm, boolean delete) {
		// check permission
		if (!canDeleteGraphModel(subject, pyroProject)) {
			return false;
        }
        
        // decouple from folder
        if(parent instanceof entity.core.PyroFolderDB) {
			entity.core.PyroFolderDB parentFolder = (entity.core.PyroFolderDB) parent;
			if(parentFolder.files_Hierarchy.contains(gm)){
				parentFolder.files_Hierarchy.remove(gm);
				parentFolder.persist();
			}
		} else if(parent instanceof entity.core.PyroProjectDB) {
			entity.core.PyroProjectDB project = (entity.core.PyroProjectDB) parent;
			if(project.files_Hierarchy.contains(gm)){
				project.files_Hierarchy.remove(gm);
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
	
	private void removeContainer(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		graph.delete();
	}

	@javax.ws.rs.POST
	@javax.ws.rs.Path("message/{graphModelId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response receiveMessage(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("graphModelId") long graphModelId, Message m) {
	    final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
	    final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(graphModelId);
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

	private Response executePropertyUpdate(PropertyMessage pm,entity.core.PyroUserDB user, entity.hierarchy.HierarchyDB graph) {
	    HierarchyCommandExecuter executer = new HierarchyCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
	    info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    HierarchyFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
		
        
        String type = pm.getDelegate().get__type();
		if (type.equals("hierarchy.ContA")){
			entity.hierarchy.ContADB target = entity.hierarchy.ContADB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.ContA targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.ContAImpl(target,executer);
			executer.updateContA(targetAPI, (info.scce.pyro.hierarchy.rest.ContA) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.EdgeA")){
			entity.hierarchy.EdgeADB target = entity.hierarchy.EdgeADB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.EdgeA targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.EdgeAImpl(target,executer);
			executer.updateEdgeA(targetAPI, (info.scce.pyro.hierarchy.rest.EdgeA) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.D")){
			entity.hierarchy.DDB target = entity.hierarchy.DDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.D targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.DImpl(target,executer);
			executer.updateD(targetAPI, (info.scce.pyro.hierarchy.rest.D) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.Cont")){
			entity.hierarchy.ContDB target = entity.hierarchy.ContDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.Cont targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.ContImpl(target,executer);
			executer.updateCont(targetAPI, (info.scce.pyro.hierarchy.rest.Cont) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.ContD")){
			entity.hierarchy.ContDDB target = entity.hierarchy.ContDDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.ContD targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.ContDImpl(target,executer);
			executer.updateContD(targetAPI, (info.scce.pyro.hierarchy.rest.ContD) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.EdgeD")){
			entity.hierarchy.EdgeDDB target = entity.hierarchy.EdgeDDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.EdgeD targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.EdgeDImpl(target,executer);
			executer.updateEdgeD(targetAPI, (info.scce.pyro.hierarchy.rest.EdgeD) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.A")){
			entity.hierarchy.ADB target = entity.hierarchy.ADB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.A targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.AImpl(target,executer);
			executer.updateA(targetAPI, (info.scce.pyro.hierarchy.rest.A) pm.getDelegate());
		} else 
		if (type.equals("hierarchy.Hierarchy")){
			entity.hierarchy.HierarchyDB target = entity.hierarchy.HierarchyDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.hierarchy.hierarchy.Hierarchy targetAPI = new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(target,executer);
			executer.updateHierarchy(targetAPI, (info.scce.pyro.hierarchy.rest.Hierarchy) pm.getDelegate());
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
	
	public HierarchyCommandExecuter buildExecuter(PanacheEntity ie,SecurityContext securityContext){
		PanacheEntity mec = ie;
		while (mec!=null) {
			if(mec instanceof entity.hierarchy.HierarchyDB) {
				entity.hierarchy.HierarchyDB container = (entity.hierarchy.HierarchyDB) mec;
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				return new HierarchyCommandExecuter(user, objectCache, graphModelWebSocket, container, new java.util.LinkedList<>());
			} else if(mec instanceof entity.hierarchy.ContADB) {
				entity.hierarchy.ContADB container = (entity.hierarchy.ContADB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.hierarchy.ContDB) {
				entity.hierarchy.ContDB container = (entity.hierarchy.ContDB) mec;
				mec = container.getContainer();
			} else if(mec instanceof entity.hierarchy.ContDDB) {
				entity.hierarchy.ContDDB container = (entity.hierarchy.ContDDB) mec;
				mec = container.getContainer();
			}
			else {
				break;
			}
			
		}
		throw new IllegalStateException("Graphmodel could not be found");
	}
	
	private void createNode(String type,Object mec, long x, long y, Long primeId, HierarchyCommandExecuter executer,SecurityContext securityContext) {
		if(mec instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
			info.scce.cinco.product.hierarchy.hierarchy.ContA n = (info.scce.cinco.product.hierarchy.hierarchy.ContA) mec;
			if(type.equals("hierarchy.A")) {
				n.newA(
				(int)x,
				(int)y);
			} else 
			if(type.equals("hierarchy.D")) {
				n.newD(
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
			info.scce.cinco.product.hierarchy.hierarchy.Cont n = (info.scce.cinco.product.hierarchy.hierarchy.Cont) mec;
			if(type.equals("hierarchy.A")) {
				n.newA(
				(int)x,
				(int)y);
			} else 
			if(type.equals("hierarchy.D")) {
				n.newD(
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
			info.scce.cinco.product.hierarchy.hierarchy.ContD n = (info.scce.cinco.product.hierarchy.hierarchy.ContD) mec;
			if(type.equals("hierarchy.D")) {
				n.newD(
				(int)x,
				(int)y);
			} else 
			if(type.equals("hierarchy.A")) {
				n.newA(
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
			info.scce.cinco.product.hierarchy.hierarchy.Hierarchy n = (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) mec;
			if(type.equals("hierarchy.ContA")) {
				n.newContA(
				(int)x,
				(int)y);
			} else 
			if(type.equals("hierarchy.Cont")) {
				n.newCont(
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
		
		if(delegate instanceof entity.hierarchy.EdgeADB) {
			entity.hierarchy.EdgeADB edge = (entity.hierarchy.EdgeADB) delegate;
			edge.bendingPoints.clear();
			edge.bendingPoints.addAll(bpEntities);
		} else if(delegate instanceof entity.hierarchy.EdgeDDB) {
			entity.hierarchy.EdgeDDB edge = (entity.hierarchy.EdgeDDB) delegate;
			edge.bendingPoints.clear();
			edge.bendingPoints.addAll(bpEntities);
		}
		
		delegate.persist();
	}
	
	private graphmodel.Edge createEdge(String type, graphmodel.Node source, graphmodel.Node target, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions, HierarchyCommandExecuter executer) {
		graphmodel.Edge edge = null;
		
		
		return edge;
	}
	
    private Response executeCommand(CompoundCommandMessage ccm, entity.core.PyroUserDB user, entity.hierarchy.HierarchyDB graph,SecurityContext securityContext) {
        //setup batch execution
        HierarchyCommandExecuter executer = new HierarchyCommandExecuter(user,objectCache,graphModelWebSocket,graph,ccm.getHighlightings());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
        HierarchyFactory.eINSTANCE.warmup(graphModelController.getProject(graph),projectWebSocket,user,executer);
        
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
							if(c.getType().equals("hierarchy.ContA")) {
								n = executer.createContA(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hierarchy.rest.ContA) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("hierarchy.D")) {
								n = executer.createD(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hierarchy.rest.D) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("hierarchy.Cont")) {
								n = executer.createCont(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hierarchy.rest.Cont) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("hierarchy.ContD")) {
								n = executer.createContD(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hierarchy.rest.ContD) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("hierarchy.A")) {
								n = executer.createA(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.hierarchy.rest.A) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							}
						} else {
							if(c.getType().equals("hierarchy.ContA")) {
								executer.createContA(
									cm.getX(),
									cm.getY(),
									400,
									100,
									cmec,
									(info.scce.pyro.hierarchy.rest.ContA)cm.getElement());
							} else if(c.getType().equals("hierarchy.D")) {
								executer.createD(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.hierarchy.rest.D)cm.getElement());
							} else if(c.getType().equals("hierarchy.Cont")) {
								executer.createCont(
									cm.getX(),
									cm.getY(),
									400,
									100,
									cmec,
									(info.scce.pyro.hierarchy.rest.Cont)cm.getElement());
							} else if(c.getType().equals("hierarchy.ContD")) {
								executer.createContD(
									cm.getX(),
									cm.getY(),
									400,
									100,
									cmec,
									(info.scce.pyro.hierarchy.rest.ContD)cm.getElement());
							} else if(c.getType().equals("hierarchy.A")) {
								executer.createA(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.hierarchy.rest.A)cm.getElement());
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
					if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
						info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiEdge = (info.scce.cinco.product.hierarchy.hierarchy.EdgeA) ce;
						executer.updateEdgeA(apiEdge, cm.getPositions());
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
						info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiEdge = (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) ce;
						executer.updateEdgeD(apiEdge, cm.getPositions());
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
					if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
						executer.updateContA(
							(info.scce.cinco.product.hierarchy.hierarchy.ContA) ce,
							(info.scce.pyro.hierarchy.rest.ContA) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
						executer.updateEdgeA(
							(info.scce.cinco.product.hierarchy.hierarchy.EdgeA) ce,
							(info.scce.pyro.hierarchy.rest.EdgeA) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
						executer.updateD(
							(info.scce.cinco.product.hierarchy.hierarchy.D) ce,
							(info.scce.pyro.hierarchy.rest.D) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
						executer.updateCont(
							(info.scce.cinco.product.hierarchy.hierarchy.Cont) ce,
							(info.scce.pyro.hierarchy.rest.Cont) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
						executer.updateContD(
							(info.scce.cinco.product.hierarchy.hierarchy.ContD) ce,
							(info.scce.pyro.hierarchy.rest.ContD) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
						executer.updateEdgeD(
							(info.scce.cinco.product.hierarchy.hierarchy.EdgeD) ce,
							(info.scce.pyro.hierarchy.rest.EdgeD) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
						executer.updateA(
							(info.scce.cinco.product.hierarchy.hierarchy.A) ce,
							(info.scce.pyro.hierarchy.rest.A) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
						executer.updateHierarchy(
							(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) ce,
							(info.scce.pyro.hierarchy.rest.Hierarchy) cm.getElement()
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
    
    private Response createResponse(String type,HierarchyCommandExecuter executer,long userId,long graphId,java.util.List<RewriteRule> rewriteRuleList) {
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
		.list("user = ?1 and project = ?2 and graphModelType = ?3",user,project,entity.core.PyroGraphModelTypeDB.HIERARCHY);
		return result.size() < 1 ? false : result.get(0).permissions.contains(operation);
    }
}

