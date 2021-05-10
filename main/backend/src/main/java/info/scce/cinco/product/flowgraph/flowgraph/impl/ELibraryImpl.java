package info.scce.cinco.product.flowgraph.flowgraph.impl;

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.FlowGraphCommandExecuter;

public class ELibraryImpl implements info.scce.cinco.product.flowgraph.flowgraph.ELibrary {
	
	private final entity.flowgraph.ELibraryDB delegate;
	private final FlowGraphCommandExecuter cmdExecuter;

	public ELibraryImpl(
		entity.flowgraph.ELibraryDB delegate,
		FlowGraphCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public ELibraryImpl(
		FlowGraphCommandExecuter cmdExecuter	) {
		this.delegate = new entity.flowgraph.ELibraryDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary
			&& ((info.scce.cinco.product.flowgraph.flowgraph.ELibrary) obj).getId().equals(getId());
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
	public entity.flowgraph.ELibraryDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph getRootElement() {
		return this.getContainer();
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph getContainer() {
		return (info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		removeEdges();
		cmdExecuter.removeELibrary(this,this.getLibrary());
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
		if(c instanceof entity.flowgraph.FlowGraphDB) {
			entity.flowgraph.FlowGraphDB container = (entity.flowgraph.FlowGraphDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		}
	}
	
	private void setContainer(PanacheEntity c) {
		if(c instanceof entity.flowgraph.FlowGraphDB) {
			entity.flowgraph.FlowGraphDB newContainer = (entity.flowgraph.FlowGraphDB) c;
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
	
	@Override
	public externallibrary.ExternalActivityLibrary getLibrary()
	{
		PanacheEntity entity = delegate.getLibrary();
		return (externallibrary.ExternalActivityLibrary) TypeRegistry.getDBToApi(entity, cmdExecuter);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Transition> getIncomingTransitions() {
		return getIncoming(info.scce.cinco.product.flowgraph.flowgraph.Transition.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition> getIncomingLabeledTransitions() {
		return getIncoming(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Start> getStartPredecessors() {
		return getPredecessors(info.scce.cinco.product.flowgraph.flowgraph.Start.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Activity> getActivityPredecessors() {
		return getPredecessors(info.scce.cinco.product.flowgraph.flowgraph.Activity.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.EActivityA> getEActivityAPredecessors() {
		return getPredecessors(info.scce.cinco.product.flowgraph.flowgraph.EActivityA.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.EActivityB> getEActivityBPredecessors() {
		return getPredecessors(info.scce.cinco.product.flowgraph.flowgraph.EActivityB.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.ELibrary> getELibraryPredecessors() {
		return getPredecessors(info.scce.cinco.product.flowgraph.flowgraph.ELibrary.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph> getSubFlowGraphPredecessors() {
		return getPredecessors(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition> getOutgoingLabeledTransitions() {
		return getOutgoing(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class);
	}
				
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition newLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.End target) {
		info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition cn = cmdExecuter.createLabeledTransition(this, target, java.util.Collections.emptyList(), null);
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition newLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.Activity target) {
		info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition cn = cmdExecuter.createLabeledTransition(this, target, java.util.Collections.emptyList(), null);
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition newLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.EActivityA target) {
		info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition cn = cmdExecuter.createLabeledTransition(this, target, java.util.Collections.emptyList(), null);
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition newLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.EActivityB target) {
		info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition cn = cmdExecuter.createLabeledTransition(this, target, java.util.Collections.emptyList(), null);
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition newLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.ELibrary target) {
		info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition cn = cmdExecuter.createLabeledTransition(this, target, java.util.Collections.emptyList(), null);
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition newLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph target) {
		info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition cn = cmdExecuter.createLabeledTransition(this, target, java.util.Collections.emptyList(), null);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.End> getEndSuccessors() {
		return getSuccessors(info.scce.cinco.product.flowgraph.flowgraph.End.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Activity> getActivitySuccessors() {
		return getSuccessors(info.scce.cinco.product.flowgraph.flowgraph.Activity.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.EActivityA> getEActivityASuccessors() {
		return getSuccessors(info.scce.cinco.product.flowgraph.flowgraph.EActivityA.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.EActivityB> getEActivityBSuccessors() {
		return getSuccessors(info.scce.cinco.product.flowgraph.flowgraph.EActivityB.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.ELibrary> getELibrarySuccessors() {
		return getSuccessors(info.scce.cinco.product.flowgraph.flowgraph.ELibrary.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph> getSubFlowGraphSuccessors() {
		return getSuccessors(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class);
	}
}
