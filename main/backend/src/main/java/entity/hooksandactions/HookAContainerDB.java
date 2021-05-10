package entity.hooksandactions;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hooksandactions_hookacontainer")
public class HookAContainerDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HooksAndActionsDB container_HooksAndActions;
	
	@javax.persistence.OneToMany(mappedBy="target_HookAContainer")
	public java.util.Collection<entity.hooksandactions.HookAnEdgeDB> incoming_HookAnEdge = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="source_HookAContainer")
	public java.util.Collection<entity.hooksandactions.HookAnEdgeDB> outgoing_HookAnEdge = new java.util.ArrayList<>();
	
	public String attribute;
	
	public PanacheEntity getContainer() {
		if(container_HooksAndActions != null) {
			return container_HooksAndActions;
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
			if(container_HooksAndActions != null && !container_HooksAndActions.equals(e)) {
				container_HooksAndActions.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
			// null all other types
			// set element
			container_HooksAndActions = (entity.hooksandactions.HooksAndActionsDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_HooksAndActions = null;
	}
	
	public java.util.Collection<PanacheEntity> getIncoming() {
		java.util.Collection<PanacheEntity> incoming = new java.util.ArrayList<>();	
		incoming.addAll(incoming_HookAnEdge);
		return incoming;
	}
	
	public void clearIncoming() {
		clearIncoming(false);
	}
	
	public void clearIncoming(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hooksandactions.HookAnEdgeDB> iter_incoming_HookAnEdge = incoming_HookAnEdge.iterator();
			while(iter_incoming_HookAnEdge.hasNext()) {
				entity.hooksandactions.HookAnEdgeDB e = iter_incoming_HookAnEdge.next();
				if(e != null) {
					e.delete();
					incoming_HookAnEdge.remove(e);
				}
				iter_incoming_HookAnEdge = incoming_HookAnEdge.iterator();
			}
		} else {
			// clear all collections
			incoming_HookAnEdge.clear();
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
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			incoming_HookAnEdge.add((entity.hooksandactions.HookAnEdgeDB) e);
		}
	}
	
	public boolean removeIncoming(PanacheEntity e) {
		return removeIncoming(e, false);
	}
	
	public boolean removeIncoming(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB definitiveEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			if(incoming_HookAnEdge.contains(definitiveEntity)) {
				boolean result = incoming_HookAnEdge.remove(definitiveEntity);
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
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB definitiveEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			return incoming_HookAnEdge.contains(definitiveEntity);
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
		outgoing.addAll(outgoing_HookAnEdge);
		return outgoing;
	}
	
	public void clearOutgoing() {
		clearOutgoing(false);
	}
	
	public void clearOutgoing(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hooksandactions.HookAnEdgeDB> iter_outgoing_HookAnEdge = outgoing_HookAnEdge.iterator();
			while(iter_outgoing_HookAnEdge.hasNext()) {
				entity.hooksandactions.HookAnEdgeDB e = iter_outgoing_HookAnEdge.next();
				if(e != null) {
					e.delete();
					outgoing_HookAnEdge.remove(e);
				}
				iter_outgoing_HookAnEdge = outgoing_HookAnEdge.iterator();
			}
		} else {
			// clear all collections
			outgoing_HookAnEdge.clear();
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
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			outgoing_HookAnEdge.add((entity.hooksandactions.HookAnEdgeDB) e);
		}
	}
	
	public boolean removeOutgoing(PanacheEntity e) {
		return removeOutgoing(e, false);
	}
	
	public boolean removeOutgoing(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB definitiveEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			if(outgoing_HookAnEdge.contains(definitiveEntity)) {
				boolean result = outgoing_HookAnEdge.remove(definitiveEntity);
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
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB definitiveEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			return outgoing_HookAnEdge.contains(definitiveEntity);
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
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB container = (entity.hooksandactions.HooksAndActionsDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// decouple from incoming
		this.clearIncoming(true);
		
		// decouple from outgoing
		this.clearOutgoing(true);
		
		// delete entity
		super.delete();
	}
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		// no attributes
		return modelElements;
	}
	
	public void clearModelElements() {
		clearModelElements(false);
	}
	
	public void clearModelElements(boolean delete) {
		// no attributes
	}
	
	public void setModelElements(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearModelElements();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addModelElements(e);
		}
	}
	
	public void addAllModelElements(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addModelElements(e);
		}
	}
	
	public void addModelElements(PanacheEntity e) {
		// add the entity into it's type-specific list
		// no attributes
	}
	
	public boolean removeModelElements(PanacheEntity e) {
		return removeModelElements(e, false);
	}
	
	public boolean removeModelElements(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		// no attributes
		return false;
	}
	
	public boolean containsModelElements(PanacheEntity e) {
		// containment-check of the entities type-specific list
		// no attributes
		return false;
	}
	
	public boolean isEmptyModelElements() {
		return getModelElements().isEmpty();
	}
	
	public int sizeModelElements() {
		return getModelElements().size();
	}
}
