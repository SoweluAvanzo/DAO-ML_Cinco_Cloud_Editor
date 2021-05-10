package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_start")
public class StartDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.SwimlaneDB container_Swimlane;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.FlowGraphDB container_FlowGraph;
	
	@javax.persistence.OneToMany(mappedBy="source_Start")
	public java.util.Collection<entity.flowgraph.TransitionDB> outgoing_Transition = new java.util.ArrayList<>();
	
	public PanacheEntity getContainer() {
		if(container_Swimlane != null) {
			return container_Swimlane;
		} else if(container_FlowGraph != null) {
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
			if(container_Swimlane != null && !container_Swimlane.equals(e)) {
				container_Swimlane.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(container_FlowGraph != null && !container_FlowGraph.equals(e)) {
				container_FlowGraph.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			// null all other types
			container_FlowGraph = null;
			// set element
			container_Swimlane = (entity.flowgraph.SwimlaneDB) e;
			return;
		} else if(e instanceof entity.flowgraph.FlowGraphDB) {
			// null all other types
			container_Swimlane = null;
			// set element
			container_FlowGraph = (entity.flowgraph.FlowGraphDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_Swimlane = null;
		container_FlowGraph = null;
	}
	
	public java.util.Collection<PanacheEntity> getIncoming() {
		java.util.Collection<PanacheEntity> incoming = new java.util.ArrayList<>();	
		// no attributes
		return incoming;
	}
	
	public void clearIncoming() {
		clearIncoming(false);
	}
	
	public void clearIncoming(boolean delete) {
		// no attributes
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
		// no attributes
	}
	
	public boolean removeIncoming(PanacheEntity e) {
		return removeIncoming(e, false);
	}
	
	public boolean removeIncoming(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		// no attributes
		return false;
	}
	
	public boolean containsIncoming(PanacheEntity e) {
		// containment-check of the entities type-specific list
		// no attributes
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
		outgoing.addAll(outgoing_Transition);
		return outgoing;
	}
	
	public void clearOutgoing() {
		clearOutgoing(false);
	}
	
	public void clearOutgoing(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.flowgraph.TransitionDB> iter_outgoing_Transition = outgoing_Transition.iterator();
			while(iter_outgoing_Transition.hasNext()) {
				entity.flowgraph.TransitionDB e = iter_outgoing_Transition.next();
				if(e != null) {
					e.delete();
					outgoing_Transition.remove(e);
				}
				iter_outgoing_Transition = outgoing_Transition.iterator();
			}
		} else {
			// clear all collections
			outgoing_Transition.clear();
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
		if(e instanceof entity.flowgraph.TransitionDB) {
			outgoing_Transition.add((entity.flowgraph.TransitionDB) e);
		}
	}
	
	public boolean removeOutgoing(PanacheEntity e) {
		return removeOutgoing(e, false);
	}
	
	public boolean removeOutgoing(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB definitiveEntity = (entity.flowgraph.TransitionDB) e;
			if(outgoing_Transition.contains(definitiveEntity)) {
				boolean result = outgoing_Transition.remove(definitiveEntity);
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
		if(e instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB definitiveEntity = (entity.flowgraph.TransitionDB) e;
			return outgoing_Transition.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyOutgoing() {
		return getOutgoing().isEmpty();
	}
	
	public int sizeOutgoing() {
		return getOutgoing().size();
	}
	
	@Override
	public void delete() {
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB container = (entity.flowgraph.SwimlaneDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		if(c instanceof entity.flowgraph.FlowGraphDB) {
			entity.flowgraph.FlowGraphDB container = (entity.flowgraph.FlowGraphDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// decouple from outgoing
		this.clearOutgoing(true);
		
		// delete entity
		super.delete();
	}
}
