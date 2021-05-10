package info.scce.cinco.product.hierarchy.hierarchy.impl;

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HierarchyCommandExecuter;

public class TDImpl implements info.scce.cinco.product.hierarchy.hierarchy.TD {
	
	private final entity.hierarchy.TDDB delegate;
	private final HierarchyCommandExecuter cmdExecuter;
	private final graphmodel.IdentifiableElement parent;
	private final info.scce.pyro.core.graphmodel.IdentifiableElement prev;

	public TDImpl(
		entity.hierarchy.TDDB delegate,
		HierarchyCommandExecuter cmdExecuter,
		graphmodel.IdentifiableElement parent,
		info.scce.pyro.core.graphmodel.IdentifiableElement prev
	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
		this.parent = parent;
		this.prev = prev;
	}
	
	public TDImpl(
		HierarchyCommandExecuter cmdExecuter,
		graphmodel.IdentifiableElement parent,
		info.scce.pyro.core.graphmodel.IdentifiableElement prev
	) {
		this.delegate = new entity.hierarchy.TDDB();
		this.parent = parent;
		this.prev = prev;
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.hierarchy.hierarchy.TD
			&& ((info.scce.cinco.product.hierarchy.hierarchy.TD) obj).getId().equals(getId());
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
	public entity.hierarchy.TDDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public String getOfTD() {
		return this.delegate.ofTD;
	}
	
	@Override
	public void setOfTD(String attr) {
		this.delegate.ofTD = attr;
		
		// commandExecuter
		this.cmdExecuter.updateIdentifiableElement(this.parent,prev);
		
		// persist
		this.delegate.persist();
	}
}
