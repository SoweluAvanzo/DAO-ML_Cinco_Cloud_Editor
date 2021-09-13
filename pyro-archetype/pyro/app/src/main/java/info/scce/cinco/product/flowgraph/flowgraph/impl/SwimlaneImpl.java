package info.scce.cinco.product.flowgraph.flowgraph.impl;

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter;

public class SwimlaneImpl implements info.scce.cinco.product.flowgraph.flowgraph.Swimlane {
	
	private final entity.flowgraph.SwimlaneDB delegate;
	
	private final CommandExecuter cmdExecuter;
	
	
	public SwimlaneImpl(
		entity.flowgraph.SwimlaneDB delegate,
		CommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public SwimlaneImpl(
		CommandExecuter cmdExecuter	) {
		this.delegate = new entity.flowgraph.SwimlaneDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane
			&& ((info.scce.cinco.product.flowgraph.flowgraph.Swimlane) obj).getId().equals(getId());
	}
	
	@Override
	public int hashCode() {
		return delegate.id.intValue();
	}
	
	@Override
	public String getId() {
		return Long.toString(this.delegate.id);
	}
	
	@Override
	public long getDelegateId() {
		return this.delegate.id;
	}
	
	@Override
	public entity.flowgraph.SwimlaneDB getDelegate() {
		return this.delegate;
	}
	
	
	public info.scce.cinco.product.flowgraph.flowgraph.Swimlane getSwimlaneView() {
		return this;
	}
	
	public info.scce.cinco.product.flowgraph.flowgraph.Swimlane eClass() {
		return this;
	}
	
	@Override 
	public graphmodel.GraphModel getRootElement() {
		graphmodel.ModelElementContainer container = this.getContainer();
		if(container instanceof graphmodel.GraphModel){
			return (graphmodel.GraphModel) container;
		} else if(container instanceof graphmodel.ModelElement) {
			graphmodel.ModelElement parent = (graphmodel.ModelElement) container;
			return (graphmodel.GraphModel) parent.getRootElement();
		} else {
			return null;
		}
	}
	
	@Override
	public graphmodel.ModelElementContainer getContainer() {
		return (graphmodel.ModelElementContainer) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		removeEdges();
		removeNodes();
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			flowgraphdiagramCommandExecuter.removeSwimlane(this);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		this.delegate.delete();
	}
	
	private void removeEdges() {
		java.util.List<graphmodel.Edge> outgoing = this.getOutgoing();
		java.util.List<graphmodel.Edge> incoming = this.getIncoming();
		
		java.util.Iterator<graphmodel.Edge> iter_outgoing = outgoing.iterator();
		while(iter_outgoing.hasNext()) {
			graphmodel.Edge e = iter_outgoing.next();
			if(e != null) {
				e.delete();
				outgoing.remove(e);
			}
			iter_outgoing = outgoing.iterator();
		}
		
		java.util.Iterator<graphmodel.Edge> iter_incoming = incoming.iterator();
		while(iter_incoming.hasNext()) {
			graphmodel.Edge e = iter_incoming.next();
			if(e != null) {
				e.delete();
				incoming.remove(e);
			}
			iter_incoming = incoming.iterator();
		}
	}
	
	@Override
	public int getX() {
	    return (int)this.delegate.x;
	}
	
	@Override
	public int getY() {
	    return (int)this.delegate.y;
	}
	
	@Override
	public int getWidth() {
	    return (int)this.delegate.width;
	}
	
	@Override
	public int getHeight() {
	    return (int)this.delegate.height;
	}
	
	@Override
	public java.util.List<graphmodel.Edge> getIncoming() {
		java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
		java.util.Collection<PanacheEntity> incoming = this.delegate.getIncoming();
		for(PanacheEntity e : incoming) {
			graphmodel.Edge edge = (graphmodel.Edge) TypeRegistry.getDBToApi(e, cmdExecuter);
			edges.add(edge);
		}
		return edges;
	}
	
	@Override
	public <T extends graphmodel.Edge> java.util.List<T> getIncoming(Class<T> clazz) {
	    return getIncoming().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Node> getPredecessors() {
	    return getIncoming().stream().map(n->n.getSourceElement()).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public <T extends graphmodel.Node> java.util.List<T> getPredecessors(Class<T> clazz) {
	   return getPredecessors().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Edge> getOutgoing() {
		java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
		java.util.Collection<PanacheEntity> outgoing = this.delegate.getOutgoing();
		for(PanacheEntity e : outgoing) {
			graphmodel.Edge edge = (graphmodel.Edge) TypeRegistry.getDBToApi(e, cmdExecuter);
			edges.add(edge);
		}
		return edges;
	}
	
	@Override
	public <T extends graphmodel.Edge> java.util.List<T> getOutgoing(Class<T> clazz) {
	   return getOutgoing().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Node> getSuccessors() {
	    return getOutgoing().stream().map(n->n.getTargetElement()).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public <T extends graphmodel.Node> java.util.List<T> getSuccessors(Class<T> clazz) {
	    return getSuccessors().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void move(int x, int y) {
		this.moveTo(this.getContainer(),x,y);
	}
	
	private void changeContainer(PanacheEntity newContainer) {
		PanacheEntity c = this.delegate.getContainer();
		if(c instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB container = (entity.flowgraph.SwimlaneDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		} else if(c instanceof entity.flowgraph.FlowGraphDiagramDB) {
			entity.flowgraph.FlowGraphDiagramDB container = (entity.flowgraph.FlowGraphDiagramDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		}
	}
	
	private void setContainer(PanacheEntity c) {
		if(c instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB newContainer = (entity.flowgraph.SwimlaneDB) c;
			newContainer.addModelElements(this.delegate);
			this.delegate.setContainer(newContainer);
		} else if(c instanceof entity.flowgraph.FlowGraphDiagramDB) {
			entity.flowgraph.FlowGraphDiagramDB newContainer = (entity.flowgraph.FlowGraphDiagramDB) c;
			newContainer.addModelElements(this.delegate);
			this.delegate.setContainer(newContainer);
		}
	}
	
	@Override
	public void moveTo(graphmodel.ModelElementContainer container,int x, int y) {
		// command executer
		this.cmdExecuter.moveNode(
			TypeRegistry.getTypeOf(this),
			this,
			container,
			TypeRegistry.getTypeOf(container),
			TypeRegistry.getTypeOf(this.getContainer()),
			x,
			y
		);
		
		// changes
		PanacheEntity oldContainer = this.delegate.getContainer();
		PanacheEntity newContainer = TypeRegistry.getApiToDB(container);
		this.changeContainer(newContainer);
		this.delegate.x = x;
		this.delegate.y = y;
		
		// postMove
		
		// persist
		oldContainer.persist();
		newContainer.persist();
		this.delegate.persist();
	}
	
	@Override
	public void resize(int width, int height) {
		String type = TypeRegistry.getTypeOf(this);
		this.cmdExecuter.resizeNode(type, this, width, height);
		this.delegate.width = width;
		this.delegate.height = height;
		this.delegate.persist();
	}
	
	
	private void removeNodes() {
		java.util.List<graphmodel.Node> nodes = this.getModelElements(graphmodel.Node.class);
		
		java.util.Iterator<graphmodel.Node> iter_nodes = nodes.iterator();
		while(iter_nodes.hasNext()) {
			graphmodel.Node e = iter_nodes.next();
			if(e != null) {
				e.delete();
				nodes.remove(e);
			}
			iter_nodes = nodes.iterator();
		}
	}
	
	@Override
	public java.util.List<graphmodel.ModelElement> getModelElements() {
		java.util.List<graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
		java.util.Collection<PanacheEntity> m = this.delegate.getModelElements();
		for(PanacheEntity e : m) {
			graphmodel.ModelElement apiE = (graphmodel.ModelElement) TypeRegistry.getDBToApi(e, cmdExecuter);
			modelElements.add(apiE);
		}
		return modelElements;
	}
		
	@Override
	public <T extends graphmodel.ModelElement> java.util.List<T> getModelElements(Class<T> clazz) {
		return this.getModelElements().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	private java.util.List<graphmodel.ModelElement> getAllModelElements(graphmodel.ModelElementContainer cmc) {
		java.util.List<graphmodel.ModelElement> cm = new java.util.LinkedList<>(cmc.getModelElements());
		cm.addAll(cmc.getModelElements().stream()
			.filter(n -> n instanceof graphmodel.ModelElementContainer)
			.flatMap(n->
				getAllModelElements((graphmodel.ModelElementContainer)n).stream()
			).collect(java.util.stream.Collectors.toList()));
		return cm;
	}
	
	@Override
	public <T extends graphmodel.Edge> java.util.List<T> getEdges(Class<T> clazz) {
		return getModelElements(clazz);
	}
	
	@Override
	public <T extends graphmodel.Node> java.util.List<T> getNodes(Class<T> clazz) {
		return getModelElements(clazz);
	}
	
	@Override
	public java.util.List<graphmodel.Node> getNodes() {
		return getModelElements(graphmodel.Node.class);
	}
		
	@Override
	public java.util.List<graphmodel.Node> getAllNodes() {
		return getAllModelElements(this).stream()
		.filter(n->n instanceof graphmodel.Node)
		.map(n->(graphmodel.Node)n)
		.collect(java.util.stream.Collectors.toList());
	}
		
	@Override
	public java.util.List<graphmodel.Edge> getAllEdges() {
		return getAllModelElements(this).stream()
			.filter(n->n instanceof graphmodel.Edge)
			.map(n->(graphmodel.Edge)n)
			.collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Container> getAllContainers() {
		return getAllModelElements(this).stream()
			.filter(n->n instanceof graphmodel.Container)
			.map(n->(graphmodel.Container)n)
			.collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Start newStart(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.Start cn = null;
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			cn = flowgraphdiagramCommandExecuter.createStart(x,y,new Long(width),new Long(height),this,null);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Start newStart(int x, int y) {
			return this.newStart(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Start> getStarts() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.Start.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Activity newActivity(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.Activity cn = null;
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			cn = flowgraphdiagramCommandExecuter.createActivity(x,y,new Long(width),new Long(height),this,null);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.RandomActivityName hook = new info.scce.cinco.product.flowgraph.hooks.RandomActivityName();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Activity newActivity(int x, int y) {
			return this.newActivity(x,y,96,
			32
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Activity> getActivitys() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.Activity.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.End newEnd(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.End cn = null;
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			cn = flowgraphdiagramCommandExecuter.createEnd(x,y,new Long(width),new Long(height),this,null);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.End newEnd(int x, int y) {
			return this.newEnd(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.End> getEnds() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.End.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph newSubFlowGraph(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph cn = null;
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			cn = flowgraphdiagramCommandExecuter.createSubFlowGraph(x,y,
				96,
				32,
				this,
				null,
				primeId
			);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph newSubFlowGraph(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph cn = null;
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			cn = flowgraphdiagramCommandExecuter.createSubFlowGraph(
				x,
				y,
				new Long(width),
				new Long(height),
				this,
				null,
				primeId
			);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph> getSubFlowGraphs() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class);
	}
	
	public String getName() {
		return "Swimlane";
	}
	
	@Override
	public String getActor() {
		return this.delegate.actor;
	}
	
	@Override
	public void setActor(String attr) {
		info.scce.pyro.flowgraph.rest.Swimlane prev = info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(this.delegate,new info.scce.pyro.rest.ObjectCache());
		this.delegate.actor = attr;
		
		// commandExecuter
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			flowgraphdiagramCommandExecuter.updateSwimlaneProperties(this,prev);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		
		// persist
		this.delegate.persist();
	}
}
