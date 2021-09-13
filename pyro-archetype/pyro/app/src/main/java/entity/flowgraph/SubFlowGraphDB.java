package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_subflowgraph")
public class SubFlowGraphDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.SwimlaneDB container_Swimlane;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.FlowGraphDiagramDB container_FlowGraphDiagram;
	
	@javax.persistence.OneToMany(mappedBy="target_SubFlowGraph")
	public java.util.Collection<entity.flowgraph.TransitionDB> incoming_Transition = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="target_SubFlowGraph")
	public java.util.Collection<entity.flowgraph.LabeledTransitionDB> incoming_LabeledTransition = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="source_SubFlowGraph")
	public java.util.Collection<entity.flowgraph.LabeledTransitionDB> outgoing_LabeledTransition = new java.util.ArrayList<>();
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.FlowGraphDiagramDB subFlowGraph_FlowGraphDiagram;
	
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
		
		// decouple from old references
		if(old instanceof entity.flowgraph.SwimlaneDB) {
			((entity.flowgraph.SwimlaneDB) old).removeReference(this);
		} else if(old instanceof entity.flowgraph.FlowGraphDiagramDB) {
			((entity.flowgraph.FlowGraphDiagramDB) old).removeReference(this);
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
			container_Swimlane.addReference(this);
			return;
		} else if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
			// null all other types
			container_Swimlane = null;
			// set element
			container_FlowGraphDiagram = (entity.flowgraph.FlowGraphDiagramDB) e;
			container_FlowGraphDiagram.addReference(this);
			return;
		}
		
		// default-case
		// null all types
		container_Swimlane = null;
		container_FlowGraphDiagram = null;
	}
	
	public java.util.Collection<PanacheEntity> getIncoming() {
		java.util.Collection<PanacheEntity> incoming = new java.util.ArrayList<>();	
		incoming.addAll(incoming_Transition);
		incoming.addAll(incoming_LabeledTransition);
		return incoming;
	}
	
	public void clearIncoming() {
		clearIncoming(false);
	}
	
	public void clearIncoming(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.flowgraph.TransitionDB> iter_incoming_Transition = incoming_Transition.iterator();
			while(iter_incoming_Transition.hasNext()) {
				entity.flowgraph.TransitionDB e = iter_incoming_Transition.next();
				if(e != null) {
					e.delete();
					incoming_Transition.remove(e);
				}
				iter_incoming_Transition = incoming_Transition.iterator();
			}
			java.util.Iterator<entity.flowgraph.LabeledTransitionDB> iter_incoming_LabeledTransition = incoming_LabeledTransition.iterator();
			while(iter_incoming_LabeledTransition.hasNext()) {
				entity.flowgraph.LabeledTransitionDB e = iter_incoming_LabeledTransition.next();
				if(e != null) {
					e.delete();
					incoming_LabeledTransition.remove(e);
				}
				iter_incoming_LabeledTransition = incoming_LabeledTransition.iterator();
			}
		} else {
			// clear all collections
			incoming_Transition.clear();
			incoming_LabeledTransition.clear();
		}
	}
	
	public void setIncoming(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearIncoming();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addIncoming(e);
		}
	}
	
	public void addAllIncoming(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addIncoming(e);
		}
	}
	
	public void addIncoming(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.flowgraph.TransitionDB) {
			incoming_Transition.add((entity.flowgraph.TransitionDB) e);
		} else 
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			incoming_LabeledTransition.add((entity.flowgraph.LabeledTransitionDB) e);
		}
	}
	
	public boolean removeIncoming(PanacheEntity e) {
		return removeIncoming(e, false);
	}
	
	public boolean removeIncoming(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB definitiveEntity = (entity.flowgraph.TransitionDB) e;
			if(incoming_Transition.contains(definitiveEntity)) {
				boolean result = incoming_Transition.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB definitiveEntity = (entity.flowgraph.LabeledTransitionDB) e;
			if(incoming_LabeledTransition.contains(definitiveEntity)) {
				boolean result = incoming_LabeledTransition.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsIncoming(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB definitiveEntity = (entity.flowgraph.TransitionDB) e;
			return incoming_Transition.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB definitiveEntity = (entity.flowgraph.LabeledTransitionDB) e;
			return incoming_LabeledTransition.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyIncoming() {
		return getIncoming().isEmpty();
	}
	
	public int sizeIncoming() {
		return getIncoming().size();
	}
	
	public java.util.Collection<PanacheEntity> getOutgoing() {
		java.util.Collection<PanacheEntity> outgoing = new java.util.ArrayList<>();	
		outgoing.addAll(outgoing_LabeledTransition);
		return outgoing;
	}
	
	public void clearOutgoing() {
		clearOutgoing(false);
	}
	
	public void clearOutgoing(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.flowgraph.LabeledTransitionDB> iter_outgoing_LabeledTransition = outgoing_LabeledTransition.iterator();
			while(iter_outgoing_LabeledTransition.hasNext()) {
				entity.flowgraph.LabeledTransitionDB e = iter_outgoing_LabeledTransition.next();
				if(e != null) {
					e.delete();
					outgoing_LabeledTransition.remove(e);
				}
				iter_outgoing_LabeledTransition = outgoing_LabeledTransition.iterator();
			}
		} else {
			// clear all collections
			outgoing_LabeledTransition.clear();
		}
	}
	
	public void setOutgoing(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearOutgoing();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addOutgoing(e);
		}
	}
	
	public void addAllOutgoing(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addOutgoing(e);
		}
	}
	
	public void addOutgoing(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			outgoing_LabeledTransition.add((entity.flowgraph.LabeledTransitionDB) e);
		}
	}
	
	public boolean removeOutgoing(PanacheEntity e) {
		return removeOutgoing(e, false);
	}
	
	public boolean removeOutgoing(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB definitiveEntity = (entity.flowgraph.LabeledTransitionDB) e;
			if(outgoing_LabeledTransition.contains(definitiveEntity)) {
				boolean result = outgoing_LabeledTransition.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsOutgoing(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB definitiveEntity = (entity.flowgraph.LabeledTransitionDB) e;
			return outgoing_LabeledTransition.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyOutgoing() {
		return getOutgoing().isEmpty();
	}
	
	public int sizeOutgoing() {
		return getOutgoing().size();
	}
	
	public PanacheEntity getSubFlowGraph() {
		if(subFlowGraph_FlowGraphDiagram != null) {
			return subFlowGraph_FlowGraphDiagram;
		}
		return null;
	}
	
	public void setSubFlowGraph(PanacheEntity e) {
		setSubFlowGraph(e, false);
	}
	
	public void setSubFlowGraph(PanacheEntity e, boolean deleteOld) {
		// guard
		PanacheEntity old = this.getSubFlowGraph();
		// if no element to delete or the element to delete is
		// same that will be set then dont delete
		if(old != null && old.equals(e)
			|| e == null && old == null
		) {
			// nothing changed
			return;
		}
		
		// decouple from old references
		if(old instanceof entity.flowgraph.FlowGraphDiagramDB) {
			((entity.flowgraph.FlowGraphDiagramDB) old).removeReference(this);
		}
		
		// potencially delete all old elements
		if(deleteOld) {
			if(old != null) {
				old.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
			// null all other types
			// set element
			subFlowGraph_FlowGraphDiagram = (entity.flowgraph.FlowGraphDiagramDB) e;
			subFlowGraph_FlowGraphDiagram.addReference(this);
			return;
		}
		
		// default-case
		// null all types
		subFlowGraph_FlowGraphDiagram = null;
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
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.flowgraph.FlowGraphDiagramDB) {
			entity.flowgraph.FlowGraphDiagramDB container = (entity.flowgraph.FlowGraphDiagramDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// decouple from incoming
		this.clearIncoming(true);
		
		// decouple from outgoing
		this.clearOutgoing(true);
		
		// decouple from primeReference
		this.setSubFlowGraph(null);
		
		// delete entity
		super.delete();
	}
}
