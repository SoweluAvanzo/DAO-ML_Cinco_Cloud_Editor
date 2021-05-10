package info.scce.cinco.product.hierarchy.hierarchy.impl;

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HierarchyCommandExecuter;

public class TAImpl implements info.scce.cinco.product.hierarchy.hierarchy.TA {
	
	private final entity.hierarchy.TADB delegate;
	private final HierarchyCommandExecuter cmdExecuter;
	private final graphmodel.IdentifiableElement parent;
	private final info.scce.pyro.core.graphmodel.IdentifiableElement prev;

	public TAImpl(
		entity.hierarchy.TADB delegate,
		HierarchyCommandExecuter cmdExecuter,
		graphmodel.IdentifiableElement parent,
		info.scce.pyro.core.graphmodel.IdentifiableElement prev
	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
		this.parent = parent;
		this.prev = prev;
	}
	
	public TAImpl(
		HierarchyCommandExecuter cmdExecuter,
		graphmodel.IdentifiableElement parent,
		info.scce.pyro.core.graphmodel.IdentifiableElement prev
	) {
		this.delegate = new entity.hierarchy.TADB();
		this.parent = parent;
		this.prev = prev;
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.hierarchy.hierarchy.TA
			&& ((info.scce.cinco.product.hierarchy.hierarchy.TA) obj).getId().equals(getId());
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
	public entity.hierarchy.TADB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public String getOfTA() {
		return this.delegate.ofTA;
	}
	
	@Override
	public void setOfTA(String attr) {
		this.delegate.ofTA = attr;
		
		// commandExecuter
		this.cmdExecuter.updateIdentifiableElement(this.parent,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public String getOfTB() {
		return this.delegate.ofTB;
	}
	
	@Override
	public void setOfTB(String attr) {
		this.delegate.ofTB = attr;
		
		// commandExecuter
		this.cmdExecuter.updateIdentifiableElement(this.parent,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public String getOfTC() {
		return this.delegate.ofTC;
	}
	
	@Override
	public void setOfTC(String attr) {
		this.delegate.ofTC = attr;
		
		// commandExecuter
		this.cmdExecuter.updateIdentifiableElement(this.parent,prev);
		
		// persist
		this.delegate.persist();
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
