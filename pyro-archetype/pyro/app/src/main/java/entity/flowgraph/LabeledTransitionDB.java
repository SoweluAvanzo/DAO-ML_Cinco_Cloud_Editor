package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_labeledtransition")
public class LabeledTransitionDB extends PanacheEntity {
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.SwimlaneDB container_Swimlane;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.FlowGraphDiagramDB container_FlowGraphDiagram;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.SubFlowGraphDB source_SubFlowGraph;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.ActivityDB source_Activity;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.ExternalActivityDB source_ExternalActivity;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.EndDB target_End;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.SubFlowGraphDB target_SubFlowGraph;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.ActivityDB target_Activity;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.ExternalActivityDB target_ExternalActivity;
	
	@javax.persistence.OneToMany
	public java.util.Collection<entity.core.BendingPointDB> bendingPoints = new java.util.ArrayList<>();
	
	public String label;
	
	public PanacheEntity getContainer() {
		if(container_Swimlane != null) {
			return container_Swimlane;
		} else if(container_FlowGraphDiagram != null) {
			return container_FlowGraphDiagram;
		}
		return null;
	}
	
	public void setContainer(PanacheEntity e) {
		setContainer(e, false);
	}
	
	public void setContainer(PanacheEntity e, boolean deleteOld) {
		// guard
		PanacheEntity old = this.getContainer();
		// if no element to delete or the element to delete is
		// same that will be set then dont delete
		if(old != null && old.equals(e)
			|| e == null && old == null
		) {
			// nothing changed
			return;
		}
		
		// potencially delete all old elements
		if(deleteOld) {
			if(old != null) {
				old.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			// null all other types
			container_FlowGraphDiagram = null;
			// set element
			container_Swimlane = (entity.flowgraph.SwimlaneDB) e;
			return;
		} else if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
			// null all other types
			container_Swimlane = null;
			// set element
			container_FlowGraphDiagram = (entity.flowgraph.FlowGraphDiagramDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_Swimlane = null;
		container_FlowGraphDiagram = null;
	}
	
	public PanacheEntity getSource() {
		if(source_SubFlowGraph != null) {
			return source_SubFlowGraph;
		} else if(source_Activity != null) {
			return source_Activity;
		} else if(source_ExternalActivity != null) {
			return source_ExternalActivity;
		}
		return null;
	}
	
	public void setSource(PanacheEntity e) {
		setSource(e, false);
	}
	
	public void setSource(PanacheEntity e, boolean deleteOld) {
		// guard
		PanacheEntity old = this.getSource();
		// if no element to delete or the element to delete is
		// same that will be set then dont delete
		if(old != null && old.equals(e)
			|| e == null && old == null
		) {
			// nothing changed
			return;
		}
		
		// potencially delete all old elements
		if(deleteOld) {
			if(old != null) {
				old.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			// null all other types
			source_Activity = null;
			source_ExternalActivity = null;
			// set element
			source_SubFlowGraph = (entity.flowgraph.SubFlowGraphDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ActivityDB) {
			// null all other types
			source_SubFlowGraph = null;
			source_ExternalActivity = null;
			// set element
			source_Activity = (entity.flowgraph.ActivityDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ExternalActivityDB) {
			// null all other types
			source_SubFlowGraph = null;
			source_Activity = null;
			// set element
			source_ExternalActivity = (entity.flowgraph.ExternalActivityDB) e;
			return;
		}
		
		// default-case
		// null all types
		source_SubFlowGraph = null;
		source_Activity = null;
		source_ExternalActivity = null;
	}
	
	public PanacheEntity getTarget() {
		if(target_End != null) {
			return target_End;
		} else if(target_SubFlowGraph != null) {
			return target_SubFlowGraph;
		} else if(target_Activity != null) {
			return target_Activity;
		} else if(target_ExternalActivity != null) {
			return target_ExternalActivity;
		}
		return null;
	}
	
	public void setTarget(PanacheEntity e) {
		setTarget(e, false);
	}
	
	public void setTarget(PanacheEntity e, boolean deleteOld) {
		// guard
		PanacheEntity old = this.getTarget();
		// if no element to delete or the element to delete is
		// same that will be set then dont delete
		if(old != null && old.equals(e)
			|| e == null && old == null
		) {
			// nothing changed
			return;
		}
		
		// potencially delete all old elements
		if(deleteOld) {
			if(old != null) {
				old.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.EndDB) {
			// null all other types
			target_SubFlowGraph = null;
			target_Activity = null;
			target_ExternalActivity = null;
			// set element
			target_End = (entity.flowgraph.EndDB) e;
			return;
		} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			// null all other types
			target_End = null;
			target_Activity = null;
			target_ExternalActivity = null;
			// set element
			target_SubFlowGraph = (entity.flowgraph.SubFlowGraphDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ActivityDB) {
			// null all other types
			target_End = null;
			target_SubFlowGraph = null;
			target_ExternalActivity = null;
			// set element
			target_Activity = (entity.flowgraph.ActivityDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ExternalActivityDB) {
			// null all other types
			target_End = null;
			target_SubFlowGraph = null;
			target_Activity = null;
			// set element
			target_ExternalActivity = (entity.flowgraph.ExternalActivityDB) e;
			return;
		}
		
		// default-case
		// null all types
		target_End = null;
		target_SubFlowGraph = null;
		target_Activity = null;
		target_ExternalActivity = null;
	}
	
	// add referencing element
	public void addReference(PanacheEntity e) {
		if(e == null)
			return;
	}
	
	// remove referencing element
	public void removeReference(PanacheEntity e) {
		if(e == null)
			return;
	}
	
	@Override
	public void delete() {
		// remove bendingPoints
		for(entity.core.BendingPointDB b : bendingPoints) {
			b.delete();
		}
		bendingPoints.clear();
		
		// decouple from source
		PanacheEntity dbSource = this.getSource();
		if(dbSource instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB source = (entity.flowgraph.SubFlowGraphDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB source = (entity.flowgraph.ActivityDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.flowgraph.ExternalActivityDB) {
			entity.flowgraph.ExternalActivityDB source = (entity.flowgraph.ExternalActivityDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		this.setSource(null);
		
		// decouple from target
		PanacheEntity dbTarget = this.getTarget();
		if(dbTarget instanceof entity.flowgraph.EndDB) {
			entity.flowgraph.EndDB target = (entity.flowgraph.EndDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB target = (entity.flowgraph.SubFlowGraphDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB target = (entity.flowgraph.ActivityDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.ExternalActivityDB) {
			entity.flowgraph.ExternalActivityDB target = (entity.flowgraph.ExternalActivityDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		this.setTarget(null);
		
		// delete entity
		super.delete();
	}
}
