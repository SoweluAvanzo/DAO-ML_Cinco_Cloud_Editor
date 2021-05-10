package info.scce.cinco.product.ha.hooksandactions.impl;

import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;

public class HookATypeImpl implements info.scce.cinco.product.ha.hooksandactions.HookAType {
	
	private final entity.hooksandactions.HookATypeDB delegate;
	private final HooksAndActionsCommandExecuter cmdExecuter;
	private final graphmodel.IdentifiableElement parent;
	private final info.scce.pyro.core.graphmodel.IdentifiableElement prev;

	public HookATypeImpl(
		entity.hooksandactions.HookATypeDB delegate,
		HooksAndActionsCommandExecuter cmdExecuter,
		graphmodel.IdentifiableElement parent,
		info.scce.pyro.core.graphmodel.IdentifiableElement prev
	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
		this.parent = parent;
		this.prev = prev;
	}
	
	public HookATypeImpl(
		HooksAndActionsCommandExecuter cmdExecuter,
		graphmodel.IdentifiableElement parent,
		info.scce.pyro.core.graphmodel.IdentifiableElement prev
	) {
		this.delegate = new entity.hooksandactions.HookATypeDB();
		this.parent = parent;
		this.prev = prev;
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.ha.hooksandactions.HookAType
			&& ((info.scce.cinco.product.ha.hooksandactions.HookAType) obj).getId().equals(getId());
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
	public entity.hooksandactions.HookATypeDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public String getAttribute() {
		return this.delegate.attribute;
	}
	
	@Override
	public void setAttribute(String attr) {
		this.delegate.attribute = attr;
		
		// commandExecuter
		this.cmdExecuter.updateIdentifiableElement(this.parent,prev);
		
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
