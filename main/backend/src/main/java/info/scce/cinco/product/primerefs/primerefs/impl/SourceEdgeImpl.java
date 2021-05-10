package info.scce.cinco.product.primerefs.primerefs.impl;

import info.scce.cinco.product.primerefs.primerefs.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.PrimeRefsCommandExecuter;

public class SourceEdgeImpl implements info.scce.cinco.product.primerefs.primerefs.SourceEdge {
	
	private final entity.primerefs.SourceEdgeDB delegate;
	private final PrimeRefsCommandExecuter cmdExecuter;

	public SourceEdgeImpl(
		entity.primerefs.SourceEdgeDB delegate,
		PrimeRefsCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public SourceEdgeImpl(
		PrimeRefsCommandExecuter cmdExecuter	) {
		this.delegate = new entity.primerefs.SourceEdgeDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge
			&& ((info.scce.cinco.product.primerefs.primerefs.SourceEdge) obj).getId().equals(getId());
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
	public entity.primerefs.SourceEdgeDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getRootElement() {
		return this.getContainer();
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getContainer() {
		return (info.scce.cinco.product.primerefs.primerefs.PrimeRefs) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		// decouple from container
		cmdExecuter.removeSourceEdge(this);
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
}
