package info.scce.cinco.product.ha.hooksandactions.impl;

import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;

public class HookANodeImpl implements info.scce.cinco.product.ha.hooksandactions.HookANode {
	
	private final entity.hooksandactions.HookANodeDB delegate;
	private final HooksAndActionsCommandExecuter cmdExecuter;

	public HookANodeImpl(
		entity.hooksandactions.HookANodeDB delegate,
		HooksAndActionsCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public HookANodeImpl(
		HooksAndActionsCommandExecuter cmdExecuter	) {
		this.delegate = new entity.hooksandactions.HookANodeDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.ha.hooksandactions.HookANode
			&& ((info.scce.cinco.product.ha.hooksandactions.HookANode) obj).getId().equals(getId());
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
	public entity.hooksandactions.HookANodeDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HooksAndActions getRootElement() {
		return this.getContainer();
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HooksAndActions getContainer() {
		return (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		removeEdges();
		// preDeleteHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PreDelete2 hook_preDelete = new info.scce.cinco.product.flowgraph.hooks.PreDelete2();
			hook_preDelete.init(cmdExecuter);
			hook_preDelete.preDelete(this);
		}
		{
			info.scce.cinco.product.flowgraph.hooks.PreDelete hook_preDelete = new info.scce.cinco.product.flowgraph.hooks.PreDelete();
			hook_preDelete.init(cmdExecuter);
			hook_preDelete.preDelete(this);
		}
		
		cmdExecuter.removeHookANode(this);
		this.delegate.delete();
		
		// postDeleteHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostDelete2 hook_postDelete = new info.scce.cinco.product.flowgraph.hooks.PostDelete2();
			Runnable runnable = hook_postDelete.getPostDeleteFunction(this);
			runnable.run();
		}
		{
			info.scce.cinco.product.flowgraph.hooks.PostDelete hook_postDelete = new info.scce.cinco.product.flowgraph.hooks.PostDelete();
			Runnable runnable = hook_postDelete.getPostDeleteFunction(this);
			runnable.run();
		}
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
		if(c instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB container = (entity.hooksandactions.HooksAndActionsDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		}
	}
	
	private void setContainer(PanacheEntity c) {
		if(c instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB newContainer = (entity.hooksandactions.HooksAndActionsDB) c;
			newContainer.addModelElements(this.delegate);
			this.delegate.setContainer(newContainer);
		}
	}
	
	@Override
	public void moveTo(graphmodel.ModelElementContainer container,int x, int y) {
		// pre move
		graphmodel.ModelElementContainer preContainer = this.getContainer();
		int preX = this.getX();
		int preY = this.getY();
		
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
		{
			info.scce.cinco.product.flowgraph.hooks.PostMove2 hook = new info.scce.cinco.product.flowgraph.hooks.PostMove2();
			hook.init(cmdExecuter);
			hook.postMove(this,preContainer,container,x,y,x-preX,y-preY);	
		}	
		{
			info.scce.cinco.product.flowgraph.hooks.PostMove hook = new info.scce.cinco.product.flowgraph.hooks.PostMove();
			hook.init(cmdExecuter);
			hook.postMove(this,preContainer,container,x,y,x-preX,y-preY);	
		}	
		
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

				{
			info.scce.cinco.product.flowgraph.hooks.PostResize2 hook = new info.scce.cinco.product.flowgraph.hooks.PostResize2();
			hook.init(cmdExecuter);
			hook.postResize(this,width,height);
		}
		{
			info.scce.cinco.product.flowgraph.hooks.PostResize hook = new info.scce.cinco.product.flowgraph.hooks.PostResize();
			hook.init(cmdExecuter);
			hook.postResize(this,width,height);
		}
	}
	
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookAnEdge> getIncomingHookAnEdges() {
		return getIncoming(info.scce.cinco.product.ha.hooksandactions.HookAnEdge.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.AbstractHookANode> getAbstractHookANodePredecessors() {
		return getPredecessors(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookAContainer> getHookAContainerPredecessors() {
		return getPredecessors(info.scce.cinco.product.ha.hooksandactions.HookAContainer.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookANode> getHookANodePredecessors() {
		return getPredecessors(info.scce.cinco.product.ha.hooksandactions.HookANode.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookAnEdge> getOutgoingHookAnEdges() {
		return getOutgoing(info.scce.cinco.product.ha.hooksandactions.HookAnEdge.class);
	}
				
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookAnEdge newHookAnEdge(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode target) {
		info.scce.cinco.product.ha.hooksandactions.HookAnEdge cn = cmdExecuter.createHookAnEdge(this, target, java.util.Collections.emptyList(), null);
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookAnEdge newHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAContainer target) {
		info.scce.cinco.product.ha.hooksandactions.HookAnEdge cn = cmdExecuter.createHookAnEdge(this, target, java.util.Collections.emptyList(), null);
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
				
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookAnEdge newHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookANode target) {
		info.scce.cinco.product.ha.hooksandactions.HookAnEdge cn = cmdExecuter.createHookAnEdge(this, target, java.util.Collections.emptyList(), null);
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.AbstractHookANode> getAbstractHookANodeSuccessors() {
		return getSuccessors(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookAContainer> getHookAContainerSuccessors() {
		return getSuccessors(info.scce.cinco.product.ha.hooksandactions.HookAContainer.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookANode> getHookANodeSuccessors() {
		return getSuccessors(info.scce.cinco.product.ha.hooksandactions.HookANode.class);
	}
	
	@Override
	public String getAttribute() {
		return this.delegate.attribute;
	}
	
	@Override
	public void setAttribute(String attr) {
		info.scce.pyro.hooksandactions.rest.HookANode prev = info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties(this.delegate,null);
		this.delegate.attribute = attr;
		
		// commandExecuter
		this.cmdExecuter.updateHookANodeProperties(this,prev);
		
		// persist
		this.delegate.persist();
		
		//property change hook
		org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
		esf.setName("attribute");
		info.scce.cinco.product.flowgraph.hooks.PostAttributeChange hook = new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange();
		hook.init(cmdExecuter);
		if(hook.canHandleChange(this,esf)) {
			hook.handleChange(this,esf);
		}
	}
}
