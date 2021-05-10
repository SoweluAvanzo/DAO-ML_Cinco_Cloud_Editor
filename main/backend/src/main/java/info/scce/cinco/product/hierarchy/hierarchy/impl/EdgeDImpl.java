package info.scce.cinco.product.hierarchy.hierarchy.impl;

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HierarchyCommandExecuter;

public class EdgeDImpl implements info.scce.cinco.product.hierarchy.hierarchy.EdgeD {
	
	private final entity.hierarchy.EdgeDDB delegate;
	private final HierarchyCommandExecuter cmdExecuter;

	public EdgeDImpl(
		entity.hierarchy.EdgeDDB delegate,
		HierarchyCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public EdgeDImpl(
		HierarchyCommandExecuter cmdExecuter	) {
		this.delegate = new entity.hierarchy.EdgeDDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD
			&& ((info.scce.cinco.product.hierarchy.hierarchy.EdgeD) obj).getId().equals(getId());
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
	public entity.hierarchy.EdgeDDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getRootElement() {
		return this.getContainer();
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer() {
		return (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		// decouple from container
		cmdExecuter.removeEdgeD(this);
		this.delegate.delete();
	}
	
	@Override
	public graphmodel.Node getSourceElement() {
		return null;
	}
	
	@Override
	public graphmodel.Node getTargetElement() {
		return null;
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
	}
		
	public void decoupleSourceOutgoing(PanacheEntity node) {
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
	public String getOfD() {
		return this.delegate.ofD;
	}
	
	@Override
	public void setOfD(String attr) {
		info.scce.pyro.hierarchy.rest.EdgeD prev = info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(this.delegate,null);
		this.delegate.ofD = attr;
		
		// commandExecuter
		this.cmdExecuter.updateEdgeDProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.TD getTd() {
		info.scce.pyro.hierarchy.rest.EdgeD prev = info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(this.delegate,null);
		PanacheEntity attribute = this.delegate.getTd();
		return (info.scce.cinco.product.hierarchy.hierarchy.TD) TypeRegistry.getDBToApi(attribute, this.cmdExecuter, this, prev);
	}
	
	@Override
	public void setTd(info.scce.cinco.product.hierarchy.hierarchy.TD attr) {
		info.scce.pyro.hierarchy.rest.EdgeD prev = info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(this.delegate,null);
		
		// cast value
		PanacheEntity newEntity = attr.getDelegate();
		// set new value/delete old value
		this.delegate.setTd(newEntity, true);
		
		// commandExecuter
		this.cmdExecuter.updateEdgeDProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> getTdList() {
		info.scce.pyro.hierarchy.rest.EdgeD prev = info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(this.delegate,null);
		java.util.Collection<PanacheEntity> entityList = this.delegate.getTdList();
		return (java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD>) entityList.stream().map(n -> {
			return (info.scce.cinco.product.hierarchy.hierarchy.TD) TypeRegistry.getDBToApi(n, this.cmdExecuter, this, prev);
		}).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void setTdList(java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> attr) {
		info.scce.pyro.hierarchy.rest.EdgeD prev = info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(this.delegate,null);
		
		// cast values
		java.util.Collection<PanacheEntity> newList = attr.stream().map(n -> 
				n.getDelegate()
			).collect(java.util.stream.Collectors.toList());
		// delete values that are not present in newList
		this.delegate.getTdList().stream().filter(
				(e) -> !newList.contains(e)
			).forEach(
				(e) -> this.delegate.removeTdList(e, true)
			);
		// set new values
		this.delegate.setTdList(newList);
		
		// commandExecuter
		this.cmdExecuter.updateEdgeDProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
}
