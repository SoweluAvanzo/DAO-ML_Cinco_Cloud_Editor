package info.scce.cinco.product.ha.hooksandactions.impl;

import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;

public class HookAnEdgeImpl implements info.scce.cinco.product.ha.hooksandactions.HookAnEdge {
	
	private final entity.hooksandactions.HookAnEdgeDB delegate;
	private final HooksAndActionsCommandExecuter cmdExecuter;

	public HookAnEdgeImpl(
		entity.hooksandactions.HookAnEdgeDB delegate,
		HooksAndActionsCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public HookAnEdgeImpl(
		HooksAndActionsCommandExecuter cmdExecuter	) {
		this.delegate = new entity.hooksandactions.HookAnEdgeDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge
			&& ((info.scce.cinco.product.ha.hooksandactions.HookAnEdge) obj).getId().equals(getId());
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
	public entity.hooksandactions.HookAnEdgeDB getDelegate() {
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
		// preDeleteHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PreDelete hook_preDelete = new info.scce.cinco.product.flowgraph.hooks.PreDelete();
			hook_preDelete.init(cmdExecuter);
			hook_preDelete.preDelete(this);
		}
		
		// decouple from container
		cmdExecuter.removeHookAnEdge(this);
		this.delegate.delete();
		
		// postDeleteHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostDelete hook_postDelete = new info.scce.cinco.product.flowgraph.hooks.PostDelete();
			Runnable runnable = hook_postDelete.getPostDeleteFunction(this);
			runnable.run();
		}
	}
	
	@Override
	public graphmodel.Node getSourceElement() {
		return (graphmodel.Node) TypeRegistry.getDBToApi(this.delegate.getSource(), cmdExecuter);
	}
	
	@Override
	public graphmodel.Node getTargetElement() {
		return (graphmodel.Node) TypeRegistry.getDBToApi(this.delegate.getTarget(), cmdExecuter);
	}
	
	@Override
	public void reconnectSource(graphmodel.Node node) {
		PanacheEntity dbTarget = this.delegate.getTarget();
		if(dbTarget != null) {
			cmdExecuter.reconnectEdge(
					TypeRegistry.getTypeOf(this),
					this,
					node,
					this.getTargetElement(),
					TypeRegistry.getTypeOf(node),
					TypeRegistry.getTypeOf(this.getTargetElement()),
					TypeRegistry.getTypeOf(this.getSourceElement()),
					TypeRegistry.getTypeOf(this.getTargetElement())
				);
			
			// reconnect
			PanacheEntity oldSource = this.delegate.getSource();
			this.decoupleSourceOutgoing(oldSource);
			PanacheEntity newSource = TypeRegistry.getApiToDB(node);
			this.delegate.setSource(newSource);
			
			// persist
			oldSource.persist();
			newSource.persist();
			this.delegate.persist();
		}
	}
	
	@Override
	public void reconnectTarget(graphmodel.Node node) {
		PanacheEntity dbSource = this.delegate.getSource();
		if(dbSource != null) {
			// commandExecuter
			cmdExecuter.reconnectEdge(
				TypeRegistry.getTypeOf(this),
				this,
				this.getSourceElement(),
				node, 
				TypeRegistry.getTypeOf(this.getSourceElement()),
				TypeRegistry.getTypeOf(node),
				TypeRegistry.getTypeOf(this.getSourceElement()),
				TypeRegistry.getTypeOf(this.getTargetElement())
			);
			
			// reconnect
			PanacheEntity oldTarget = this.delegate.getTarget();
			this.decoupleTargetIncoming(oldTarget);
			PanacheEntity newTarget = TypeRegistry.getApiToDB(node);
			this.delegate.setTarget(newTarget);
			
			// persist
			oldTarget.persist();
			newTarget.persist();
			this.delegate.persist();
		}
	}
	
	public void decoupleTargetIncoming(PanacheEntity node) {
		if(node instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB target = (entity.hooksandactions.HookAContainerDB) node;
			target.removeIncoming(this.delegate);
			this.delegate.setTarget(null);
		} else if(node instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB target = (entity.hooksandactions.HookANodeDB) node;
			target.removeIncoming(this.delegate);
			this.delegate.setTarget(null);
		}
	}
		
	public void decoupleSourceOutgoing(PanacheEntity node) {
		if(node instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB source = (entity.hooksandactions.HookAContainerDB) node;
			source.removeOutgoing(this.delegate);
			this.delegate.setSource(null);
		} else if(node instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB source = (entity.hooksandactions.HookANodeDB) node;
			source.removeOutgoing(this.delegate);
			this.delegate.setSource(null);
		}
	}
	
	@Override
	public void addBendingPoint(long x, long y) {
		entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
		bp.x = x;
		bp.y = y;
		bp.persist();
		this.delegate.bendingPoints.add(bp);
	}
	
	@Override
	public void clearBendingPoints() {
		this.delegate.bendingPoints.clear();
	}
	
	@Override
	public java.util.List<? extends graphmodel.BendingPoint> getBendingPoints() {
		return new java.util.LinkedList<>(this.delegate.bendingPoints);
	}
	
	@Override
	public String getAttribute() {
		return this.delegate.attribute;
	}
	
	@Override
	public void setAttribute(String attr) {
		info.scce.pyro.hooksandactions.rest.HookAnEdge prev = info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties(this.delegate,null);
		this.delegate.attribute = attr;
		
		// commandExecuter
		this.cmdExecuter.updateHookAnEdgeProperties(this,prev);
		
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
