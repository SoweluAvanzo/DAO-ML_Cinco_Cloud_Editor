package info.scce.cinco.product.flowgraph.flowgraph.impl;

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter;

public class TransitionImpl implements info.scce.cinco.product.flowgraph.flowgraph.Transition {
	
	private final entity.flowgraph.TransitionDB delegate;
	
	private final CommandExecuter cmdExecuter;
	
	
	public TransitionImpl(
		entity.flowgraph.TransitionDB delegate,
		CommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public TransitionImpl(
		CommandExecuter cmdExecuter	) {
		this.delegate = new entity.flowgraph.TransitionDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition
			&& ((info.scce.cinco.product.flowgraph.flowgraph.Transition) obj).getId().equals(getId());
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
	public entity.flowgraph.TransitionDB getDelegate() {
		return this.delegate;
	}
	
	
	public info.scce.cinco.product.flowgraph.flowgraph.Transition getTransitionView() {
		return this;
	}
	
	public info.scce.cinco.product.flowgraph.flowgraph.Transition eClass() {
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
		// decouple from container
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			flowgraphdiagramCommandExecuter.removeTransition(this);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		this.delegate.delete();
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
		if(node instanceof entity.flowgraph.EndDB) {
			entity.flowgraph.EndDB target = (entity.flowgraph.EndDB) node;
			target.removeIncoming(this.delegate);
			this.delegate.setTarget(null);
		} else if(node instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB target = (entity.flowgraph.SubFlowGraphDB) node;
			target.removeIncoming(this.delegate);
			this.delegate.setTarget(null);
		} else if(node instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB target = (entity.flowgraph.ActivityDB) node;
			target.removeIncoming(this.delegate);
			this.delegate.setTarget(null);
		} else if(node instanceof entity.flowgraph.ExternalActivityDB) {
			entity.flowgraph.ExternalActivityDB target = (entity.flowgraph.ExternalActivityDB) node;
			target.removeIncoming(this.delegate);
			this.delegate.setTarget(null);
		}
	}
		
	public void decoupleSourceOutgoing(PanacheEntity node) {
		if(node instanceof entity.flowgraph.StartDB) {
			entity.flowgraph.StartDB source = (entity.flowgraph.StartDB) node;
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
	
	public String getName() {
		return "Transition";
	}
}
