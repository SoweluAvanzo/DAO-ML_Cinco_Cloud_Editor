package info.scce.cinco.product.flowgraph.flowgraph.impl;

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter;

public class LabeledTransitionImpl implements info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition {
	
	private final entity.flowgraph.LabeledTransitionDB delegate;
	
	private final CommandExecuter cmdExecuter;
	
	
	public LabeledTransitionImpl(
		entity.flowgraph.LabeledTransitionDB delegate,
		CommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public LabeledTransitionImpl(
		CommandExecuter cmdExecuter	) {
		this.delegate = new entity.flowgraph.LabeledTransitionDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition
			&& ((info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) obj).getId().equals(getId());
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
	public entity.flowgraph.LabeledTransitionDB getDelegate() {
		return this.delegate;
	}
	
	
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition getLabeledTransitionView() {
		return this;
	}
	
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition eClass() {
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
			flowgraphdiagramCommandExecuter.removeLabeledTransition(this);
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
		if(node instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB source = (entity.flowgraph.SubFlowGraphDB) node;
			source.removeOutgoing(this.delegate);
			this.delegate.setSource(null);
		} else if(node instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB source = (entity.flowgraph.ActivityDB) node;
			source.removeOutgoing(this.delegate);
			this.delegate.setSource(null);
		} else if(node instanceof entity.flowgraph.ExternalActivityDB) {
			entity.flowgraph.ExternalActivityDB source = (entity.flowgraph.ExternalActivityDB) node;
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
		return "LabeledTransition";
	}
	
	@Override
	public String getLabel() {
		return this.delegate.label;
	}
	
	@Override
	public void setLabel(String attr) {
		info.scce.pyro.flowgraph.rest.LabeledTransition prev = info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(this.delegate,new info.scce.pyro.rest.ObjectCache());
		this.delegate.label = attr;
		
		// commandExecuter
		if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
			FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
			flowgraphdiagramCommandExecuter.updateLabeledTransitionProperties(this,prev);
		}
		else
			if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		
		// persist
		this.delegate.persist();
	}
}
