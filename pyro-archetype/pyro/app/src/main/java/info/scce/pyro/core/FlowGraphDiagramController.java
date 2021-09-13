package info.scce.pyro.core;

import info.scce.pyro.core.command.types.*;
import info.scce.pyro.core.rest.types.*;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.sync.WebSocketMessage;
import javax.ws.rs.core.SecurityContext;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter;
import info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagramFactory;


@javax.transaction.Transactional
@javax.ws.rs.Path("/flowgraphdiagram")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.enterprise.context.RequestScoped
public class FlowGraphDiagramController {

	
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
        
        if(subject==null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

	    final entity.flowgraph.FlowGraphDiagramDB newGraph =  new entity.flowgraph.FlowGraphDiagramDB();
	    newGraph.filename = graph.getfilename();
        FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(subject,objectCache,graphModelWebSocket,newGraph, new java.util.LinkedList<>());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    newGraph.scale = 1.0;
	    newGraph.connector = "normal";
	    newGraph.height = 600L;
	    newGraph.width = 2000L;
	    newGraph.router = null;
	    newGraph.isPublic = false;
	    
	    //primitive init
	    newGraph.modelName = null;
	    newGraph.extension = "flowgraph";
        newGraph.persist();

		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram ce = new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(newGraph,executer);
		info.scce.cinco.product.flowgraph.hooks.InitializeFlowGraphModel ca = new info.scce.cinco.product.flowgraph.hooks.InitializeFlowGraphModel();
		ca.init(executer);
		info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram newGraphApi = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) TypeRegistry.getDBToApi(newGraph, executer);
		ca.postCreate(newGraphApi);
		return Response.ok(info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntity(newGraph,new info.scce.pyro.rest.ObjectCache())).build();

	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("read/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id) {
		
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
												
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.ok(info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntity(graph, objectCache))
				.build();
	}

	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("generate/{id}/{generatorId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response generate(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id, @javax.ws.rs.PathParam("generatorId") String generatorId) {
	
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		if (graph == null || user == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		//setup batch execution
		FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram cgraph = new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(graph,executer);
		
		if(generatorId == null || generatorId.equals("null")) {
			try {
				
				java.util.Map<String,String[]> staticResourecURLs = new java.util.HashMap<>();
				info.scce.cinco.product.flowgraph.codegen.Generate generator = new info.scce.cinco.product.flowgraph.codegen.Generate();
				generator.generateFiles(
					cgraph,
					"/src-gen/",
					"asset/static/flowgraphdiagram",
					staticResourecURLs,
					fileController
				);
				//TODO
				return javax.ws.rs.core.Response.ok(null).build();
			
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
		}
		
		return Response.status(Response.Status.EXPECTATION_FAILED).build();
	}
	
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("customaction/{id}/{elementId}/fetch/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response fetchCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId) {
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		java.util.Map<String,String> map = new java.util.HashMap<>();
		
		FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		PanacheEntity e = TypeRegistry.findById(elementId);
		
		if(e instanceof entity.flowgraph.StartDB) {
			entity.flowgraph.StartDB dbEntity = (entity.flowgraph.StartDB) e;
			info.scce.cinco.product.flowgraph.flowgraph.Start ce = new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(dbEntity,executer);
			// customAction 0
			info.scce.cinco.product.flowgraph.action.ShortestPathToEnd ca0 = new info.scce.cinco.product.flowgraph.action.ShortestPathToEnd();
			ca0.init(executer);
			if(ca0.canExecute(ce)){
				map.put("info.scce.cinco.product.flowgraph.action.ShortestPathToEnd",ca0.getName());
			}
		}
		
		return Response.ok(map).build();
	}
	
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("customaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerCustomActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		
		PanacheEntity e = TypeRegistry.findById(elementId);
		
		if(e instanceof entity.flowgraph.StartDB) {
			entity.flowgraph.StartDB dbEntity = (entity.flowgraph.StartDB) e;
			info.scce.cinco.product.flowgraph.flowgraph.Start ce = new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(dbEntity,executer);
			if(action.getFqn().equals("info.scce.cinco.product.flowgraph.action.ShortestPathToEnd")) {
				info.scce.cinco.product.flowgraph.action.ShortestPathToEnd ca = new info.scce.cinco.product.flowgraph.action.ShortestPathToEnd();
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
		
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);

		
		Response response = createResponse("basic_valid_answer",executer,user.id,graph.id, java.util.Collections.emptyList());
		return response;
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("dbaction/{id}/{elementId}/trigger/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response triggerDoubleClickActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") long id,@javax.ws.rs.PathParam("elementId") long elementId,info.scce.pyro.core.command.types.Action action) {
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if (graph == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,action.getHighlightings());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		boolean hasExecuted = false;
		
		PanacheEntity e = TypeRegistry.findById(elementId);
		
		if(e instanceof entity.flowgraph.StartDB) {
			entity.flowgraph.StartDB dbEntity = (entity.flowgraph.StartDB) e;
			info.scce.cinco.product.flowgraph.flowgraph.Start ce = new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(dbEntity,executer);
			// doubleClickAction 0
			info.scce.cinco.product.flowgraph.action.ShortestPathToEnd ca0 = new info.scce.cinco.product.flowgraph.action.ShortestPathToEnd();
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
	@javax.ws.rs.Path("remove/{id}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response removeGraphModel(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
		final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		//find parent
		final entity.flowgraph.FlowGraphDiagramDB gm = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if(gm==null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		
		boolean succeeded = removeGraphModel(subject, gm, false);
		if (!succeeded) {
			return Response.status(Response.Status.FORBIDDEN).build();
        }
		
		FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(subject,objectCache,graphModelWebSocket,gm,new java.util.LinkedList<>());
		info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
		FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		removeContainer(new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(gm,executer));
		
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
	

	
	public boolean removeGraphModel(entity.core.PyroUserDB subject,entity.flowgraph.FlowGraphDiagramDB gm) {
		return removeGraphModel(subject, gm, true);
	}
	
	public boolean removeGraphModel(entity.core.PyroUserDB subject, entity.flowgraph.FlowGraphDiagramDB gm, boolean delete) {
		
		// delete
		if(delete)
			gm.delete();
		
		return true;
	}
	
	private void removeContainer(info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram graph) {
		graph.delete();
	}

	@javax.ws.rs.POST
	@javax.ws.rs.Path("message/{graphModelId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response receiveMessage(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("graphModelId") long graphModelId, Message m) {
	    final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);
	    final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(graphModelId);
	    if(subject==null||graph==null){
	        return Response.status(Response.Status.BAD_REQUEST).build();
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
		}
	
	    return Response.status(Response.Status.BAD_REQUEST).build();
	}

	private Response executePropertyUpdate(PropertyMessage pm,entity.core.PyroUserDB user, entity.flowgraph.FlowGraphDiagramDB graph) {
	    FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,new java.util.LinkedList<>());
	    info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
	    FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
		
        
        String type = pm.getDelegate().get__type();
		if (type.equals("flowgraph.End")){
			entity.flowgraph.EndDB target = entity.flowgraph.EndDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.End targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.EndImpl(target,executer);
			executer.updateEnd(targetAPI, (info.scce.pyro.flowgraph.rest.End) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.Swimlane")){
			entity.flowgraph.SwimlaneDB target = entity.flowgraph.SwimlaneDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.SwimlaneImpl(target,executer);
			executer.updateSwimlane(targetAPI, (info.scce.pyro.flowgraph.rest.Swimlane) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.SubFlowGraph")){
			entity.flowgraph.SubFlowGraphDB target = entity.flowgraph.SubFlowGraphDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.SubFlowGraphImpl(target,executer);
			executer.updateSubFlowGraph(targetAPI, (info.scce.pyro.flowgraph.rest.SubFlowGraph) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.Transition")){
			entity.flowgraph.TransitionDB target = entity.flowgraph.TransitionDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.Transition targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.TransitionImpl(target,executer);
			executer.updateTransition(targetAPI, (info.scce.pyro.flowgraph.rest.Transition) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.Start")){
			entity.flowgraph.StartDB target = entity.flowgraph.StartDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.Start targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(target,executer);
			executer.updateStart(targetAPI, (info.scce.pyro.flowgraph.rest.Start) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.Activity")){
			entity.flowgraph.ActivityDB target = entity.flowgraph.ActivityDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.Activity targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.ActivityImpl(target,executer);
			executer.updateActivity(targetAPI, (info.scce.pyro.flowgraph.rest.Activity) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.LabeledTransition")){
			entity.flowgraph.LabeledTransitionDB target = entity.flowgraph.LabeledTransitionDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.LabeledTransitionImpl(target,executer);
			executer.updateLabeledTransition(targetAPI, (info.scce.pyro.flowgraph.rest.LabeledTransition) pm.getDelegate());
		} else 
		if (type.equals("flowgraph.FlowGraphDiagram")){
			entity.flowgraph.FlowGraphDiagramDB target = entity.flowgraph.FlowGraphDiagramDB.findById(pm.getDelegate().getId());
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram targetAPI = new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(target,executer);
			executer.updateFlowGraphDiagram(targetAPI, (info.scce.pyro.flowgraph.rest.FlowGraphDiagram) pm.getDelegate());
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
	
	public FlowGraphDiagramCommandExecuter buildExecuter(PanacheEntity ie,SecurityContext securityContext){
		PanacheEntity mec = ie;
		while (mec!=null) {
			if(mec instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB container = (entity.flowgraph.FlowGraphDiagramDB) mec;
				final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
				return new FlowGraphDiagramCommandExecuter(user, objectCache, graphModelWebSocket, container, new java.util.LinkedList<>());
			} else if(mec instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB container = (entity.flowgraph.SwimlaneDB) mec;
				mec = container.getContainer();
			}
			else {
				break;
			}
			
		}
		throw new IllegalStateException("Graphmodel could not be found");
	}
	
	private void createNode(String type,Object mec, long x, long y, Long primeId, FlowGraphDiagramCommandExecuter executer,SecurityContext securityContext) {
		if(mec instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane n = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) mec;
			if(type.equals("flowgraph.Start")) {
				n.newStart(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.Activity")) {
				n.newActivity(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.End")) {
				n.newEnd(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.SubFlowGraph")) {
				n.newSubFlowGraph(
				primeId,
				(int)x,
				(int)y);
			}
		} else if(mec instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) {
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram n = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) mec;
			if(type.equals("flowgraph.Start")) {
				n.newStart(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.End")) {
				n.newEnd(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.Activity")) {
				n.newActivity(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.Swimlane")) {
				n.newSwimlane(
				(int)x,
				(int)y);
			} else 
			if(type.equals("flowgraph.SubFlowGraph")) {
				n.newSubFlowGraph(
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
		
		if(delegate instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB edge = (entity.flowgraph.TransitionDB) delegate;
			edge.bendingPoints.clear();
			edge.bendingPoints.addAll(bpEntities);
		} else if(delegate instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB edge = (entity.flowgraph.LabeledTransitionDB) delegate;
			edge.bendingPoints.clear();
			edge.bendingPoints.addAll(bpEntities);
		}
		
		delegate.persist();
	}
	
	private graphmodel.Edge createEdge(String type, graphmodel.Node source, graphmodel.Node target, java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> positions, FlowGraphDiagramCommandExecuter executer) {
		graphmodel.Edge edge = null;
		
		if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
			if(type.equals("flowgraph.LabeledTransition")) {
				if(
					target instanceof info.scce.cinco.product.flowgraph.flowgraph.End
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity
				) {
					edge = executer.createLabeledTransition(source, target, positions, null);
				}
			}
		} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
			if(type.equals("flowgraph.Transition")) {
				if(
					target instanceof info.scce.cinco.product.flowgraph.flowgraph.End
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity
				) {
					edge = executer.createTransition(source, target, positions, null);
				}
			}
		} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
			if(type.equals("flowgraph.LabeledTransition")) {
				if(
					target instanceof info.scce.cinco.product.flowgraph.flowgraph.End
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity
					|| target instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity
				) {
					edge = executer.createLabeledTransition(source, target, positions, null);
				}
			}
		}
		
		return edge;
	}
	
    private Response executeCommand(CompoundCommandMessage ccm, entity.core.PyroUserDB user, entity.flowgraph.FlowGraphDiagramDB graph,SecurityContext securityContext) {
        //setup batch execution
        FlowGraphDiagramCommandExecuter executer = new FlowGraphDiagramCommandExecuter(user,objectCache,graphModelWebSocket,graph,ccm.getHighlightings());
        info.scce.pyro.core.highlight.HighlightFactory.eINSTANCE.warmup(executer);
        FlowGraphDiagramFactory.eINSTANCE.warmup(executer);
        
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
							if(c.getType().equals("flowgraph.End")) {
								n = executer.createEnd(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.flowgraph.rest.End) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("flowgraph.Swimlane")) {
								n = executer.createSwimlane(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.flowgraph.rest.Swimlane) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("flowgraph.SubFlowGraph")) {
								n = executer.createSubFlowGraph(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.flowgraph.rest.SubFlowGraph) cm.getElement(),
									cm.getPrimeId()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("flowgraph.Start")) {
								n = executer.createStart(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.flowgraph.rest.Start) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							} else if(c.getType().equals("flowgraph.Activity")) {
								n = executer.createActivity(
									cm.getX(),
									cm.getY(),
									cm.getWidth(),
									cm.getHeight(),
									cmec,
									(info.scce.pyro.flowgraph.rest.Activity) cm.getElement()
								);
								ccm.rewriteId(cm.getDelegateId(),n.getDelegateId());
							}
						} else {
							if(c.getType().equals("flowgraph.End")) {
								executer.createEnd(
									cm.getX(),
									cm.getY(),
									36,
									36,
									cmec,
									(info.scce.pyro.flowgraph.rest.End)cm.getElement());
							} else if(c.getType().equals("flowgraph.Swimlane")) {
								executer.createSwimlane(
									cm.getX(),
									cm.getY(),
									400,
									100,
									cmec,
									(info.scce.pyro.flowgraph.rest.Swimlane)cm.getElement());
							} else if(c.getType().equals("flowgraph.SubFlowGraph")) {
								executer.createSubFlowGraph(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.flowgraph.rest.SubFlowGraph)cm.getElement(),
									cm.getPrimeId()
									);
							} else if(c.getType().equals("flowgraph.Start")) {
								executer.createStart(
									cm.getX(),
									cm.getY(),
									36,
									36,
									cmec,
									(info.scce.pyro.flowgraph.rest.Start)cm.getElement());
							} else if(c.getType().equals("flowgraph.Activity")) {
								executer.createActivity(
									cm.getX(),
									cm.getY(),
									96,
									32,
									cmec,
									(info.scce.pyro.flowgraph.rest.Activity)cm.getElement());
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
					if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
						info.scce.cinco.product.flowgraph.flowgraph.Transition apiEdge = (info.scce.cinco.product.flowgraph.flowgraph.Transition) ce;
						executer.updateTransition(apiEdge, cm.getPositions());
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
						info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiEdge = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) ce;
						executer.updateLabeledTransition(apiEdge, cm.getPositions());
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
					if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
						executer.updateEnd(
							(info.scce.cinco.product.flowgraph.flowgraph.End) ce,
							(info.scce.pyro.flowgraph.rest.End) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
						executer.updateSwimlane(
							(info.scce.cinco.product.flowgraph.flowgraph.Swimlane) ce,
							(info.scce.pyro.flowgraph.rest.Swimlane) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
						executer.updateSubFlowGraph(
							(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) ce,
							(info.scce.pyro.flowgraph.rest.SubFlowGraph) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
						executer.updateTransition(
							(info.scce.cinco.product.flowgraph.flowgraph.Transition) ce,
							(info.scce.pyro.flowgraph.rest.Transition) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
						executer.updateStart(
							(info.scce.cinco.product.flowgraph.flowgraph.Start) ce,
							(info.scce.pyro.flowgraph.rest.Start) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
						executer.updateActivity(
							(info.scce.cinco.product.flowgraph.flowgraph.Activity) ce,
							(info.scce.pyro.flowgraph.rest.Activity) cm.getElement()
						);
					} else if(ce instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
						executer.updateLabeledTransition(
							(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) ce,
							(info.scce.pyro.flowgraph.rest.LabeledTransition) cm.getElement()
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
    
    private Response createResponse(String type,FlowGraphDiagramCommandExecuter executer,long userId,long graphId,java.util.List<RewriteRule> rewriteRuleList) {
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

