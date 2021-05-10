package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_labeledtransition")
public class LabeledTransitionDB extends PanacheEntity {
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.FlowGraphDB container_FlowGraph;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.ActivityDB source_Activity;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.EActivityADB source_EActivityA;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.EActivityBDB source_EActivityB;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.ELibraryDB source_ELibrary;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.SubFlowGraphDB source_SubFlowGraph;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.EndDB target_End;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.ActivityDB target_Activity;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.EActivityADB target_EActivityA;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.EActivityBDB target_EActivityB;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.ELibraryDB target_ELibrary;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.SubFlowGraphDB target_SubFlowGraph;
	
	@javax.persistence.OneToMany
	public java.util.Collection<entity.core.BendingPointDB> bendingPoints = new java.util.ArrayList<>();
	
	public String label;
	
	public PanacheEntity getContainer() {
		if(container_FlowGraph != null) {
			return container_FlowGraph;
		}
		return null;
	}
	
	public void setContainer(PanacheEntity e) {
		setContainer(e, false);
	}
	
	public void setContainer(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(container_FlowGraph != null && !container_FlowGraph.equals(e)) {
				container_FlowGraph.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.FlowGraphDB) {
			// null all other types
			// set element
			container_FlowGraph = (entity.flowgraph.FlowGraphDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_FlowGraph = null;
	}
	
	public PanacheEntity getSource() {
		if(source_Activity != null) {
			return source_Activity;
		} else if(source_EActivityA != null) {
			return source_EActivityA;
		} else if(source_EActivityB != null) {
			return source_EActivityB;
		} else if(source_ELibrary != null) {
			return source_ELibrary;
		} else if(source_SubFlowGraph != null) {
			return source_SubFlowGraph;
		}
		return null;
	}
	
	public void setSource(PanacheEntity e) {
		setSource(e, false);
	}
	
	public void setSource(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(source_Activity != null && !source_Activity.equals(e)) {
				source_Activity.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(source_EActivityA != null && !source_EActivityA.equals(e)) {
				source_EActivityA.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(source_EActivityB != null && !source_EActivityB.equals(e)) {
				source_EActivityB.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(source_ELibrary != null && !source_ELibrary.equals(e)) {
				source_ELibrary.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(source_SubFlowGraph != null && !source_SubFlowGraph.equals(e)) {
				source_SubFlowGraph.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.ActivityDB) {
			// null all other types
			source_EActivityA = null;
			source_EActivityB = null;
			source_ELibrary = null;
			source_SubFlowGraph = null;
			// set element
			source_Activity = (entity.flowgraph.ActivityDB) e;
			return;
		} else if(e instanceof entity.flowgraph.EActivityADB) {
			// null all other types
			source_Activity = null;
			source_EActivityB = null;
			source_ELibrary = null;
			source_SubFlowGraph = null;
			// set element
			source_EActivityA = (entity.flowgraph.EActivityADB) e;
			return;
		} else if(e instanceof entity.flowgraph.EActivityBDB) {
			// null all other types
			source_Activity = null;
			source_EActivityA = null;
			source_ELibrary = null;
			source_SubFlowGraph = null;
			// set element
			source_EActivityB = (entity.flowgraph.EActivityBDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ELibraryDB) {
			// null all other types
			source_Activity = null;
			source_EActivityA = null;
			source_EActivityB = null;
			source_SubFlowGraph = null;
			// set element
			source_ELibrary = (entity.flowgraph.ELibraryDB) e;
			return;
		} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			// null all other types
			source_Activity = null;
			source_EActivityA = null;
			source_EActivityB = null;
			source_ELibrary = null;
			// set element
			source_SubFlowGraph = (entity.flowgraph.SubFlowGraphDB) e;
			return;
		}
		
		// default-case
		// null all types
		source_Activity = null;
		source_EActivityA = null;
		source_EActivityB = null;
		source_ELibrary = null;
		source_SubFlowGraph = null;
	}
	
	public PanacheEntity getTarget() {
		if(target_End != null) {
			return target_End;
		} else if(target_Activity != null) {
			return target_Activity;
		} else if(target_EActivityA != null) {
			return target_EActivityA;
		} else if(target_EActivityB != null) {
			return target_EActivityB;
		} else if(target_ELibrary != null) {
			return target_ELibrary;
		} else if(target_SubFlowGraph != null) {
			return target_SubFlowGraph;
		}
		return null;
	}
	
	public void setTarget(PanacheEntity e) {
		setTarget(e, false);
	}
	
	public void setTarget(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_End != null && !target_End.equals(e)) {
				target_End.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_Activity != null && !target_Activity.equals(e)) {
				target_Activity.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_EActivityA != null && !target_EActivityA.equals(e)) {
				target_EActivityA.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_EActivityB != null && !target_EActivityB.equals(e)) {
				target_EActivityB.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_ELibrary != null && !target_ELibrary.equals(e)) {
				target_ELibrary.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_SubFlowGraph != null && !target_SubFlowGraph.equals(e)) {
				target_SubFlowGraph.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.EndDB) {
			// null all other types
			target_Activity = null;
			target_EActivityA = null;
			target_EActivityB = null;
			target_ELibrary = null;
			target_SubFlowGraph = null;
			// set element
			target_End = (entity.flowgraph.EndDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ActivityDB) {
			// null all other types
			target_End = null;
			target_EActivityA = null;
			target_EActivityB = null;
			target_ELibrary = null;
			target_SubFlowGraph = null;
			// set element
			target_Activity = (entity.flowgraph.ActivityDB) e;
			return;
		} else if(e instanceof entity.flowgraph.EActivityADB) {
			// null all other types
			target_End = null;
			target_Activity = null;
			target_EActivityB = null;
			target_ELibrary = null;
			target_SubFlowGraph = null;
			// set element
			target_EActivityA = (entity.flowgraph.EActivityADB) e;
			return;
		} else if(e instanceof entity.flowgraph.EActivityBDB) {
			// null all other types
			target_End = null;
			target_Activity = null;
			target_EActivityA = null;
			target_ELibrary = null;
			target_SubFlowGraph = null;
			// set element
			target_EActivityB = (entity.flowgraph.EActivityBDB) e;
			return;
		} else if(e instanceof entity.flowgraph.ELibraryDB) {
			// null all other types
			target_End = null;
			target_Activity = null;
			target_EActivityA = null;
			target_EActivityB = null;
			target_SubFlowGraph = null;
			// set element
			target_ELibrary = (entity.flowgraph.ELibraryDB) e;
			return;
		} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			// null all other types
			target_End = null;
			target_Activity = null;
			target_EActivityA = null;
			target_EActivityB = null;
			target_ELibrary = null;
			// set element
			target_SubFlowGraph = (entity.flowgraph.SubFlowGraphDB) e;
			return;
		}
		
		// default-case
		// null all types
		target_End = null;
		target_Activity = null;
		target_EActivityA = null;
		target_EActivityB = null;
		target_ELibrary = null;
		target_SubFlowGraph = null;
	}
	
	@Override
	public void delete() {
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.flowgraph.FlowGraphDB) {
			entity.flowgraph.FlowGraphDB container = (entity.flowgraph.FlowGraphDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// remove bendingPoints
		for(entity.core.BendingPointDB b : bendingPoints) {
			b.delete();
		}
		bendingPoints.clear();
		
		// decouple from source
		PanacheEntity dbSource = this.getSource();
		if(dbSource instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB source = (entity.flowgraph.ActivityDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.flowgraph.EActivityADB) {
			entity.flowgraph.EActivityADB source = (entity.flowgraph.EActivityADB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.flowgraph.EActivityBDB) {
			entity.flowgraph.EActivityBDB source = (entity.flowgraph.EActivityBDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.flowgraph.ELibraryDB) {
			entity.flowgraph.ELibraryDB source = (entity.flowgraph.ELibraryDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB source = (entity.flowgraph.SubFlowGraphDB) dbSource;
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
		if(dbTarget instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB target = (entity.flowgraph.ActivityDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.EActivityADB) {
			entity.flowgraph.EActivityADB target = (entity.flowgraph.EActivityADB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.EActivityBDB) {
			entity.flowgraph.EActivityBDB target = (entity.flowgraph.EActivityBDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.ELibraryDB) {
			entity.flowgraph.ELibraryDB target = (entity.flowgraph.ELibraryDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB target = (entity.flowgraph.SubFlowGraphDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		this.setTarget(null);
		
		// delete entity
		super.delete();
	}
}
