package entity.primerefs;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_primerefs_primetonodeflow")
public class PrimeToNodeFlowDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne
	public entity.primerefs.PrimeRefsDB container_PrimeRefs;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.ActivityDB pr_Activity;
	
	public PanacheEntity getContainer() {
		if(container_PrimeRefs != null) {
			return container_PrimeRefs;
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
			if(container_PrimeRefs != null && !container_PrimeRefs.equals(e)) {
				container_PrimeRefs.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.primerefs.PrimeRefsDB) {
			// null all other types
			// set element
			container_PrimeRefs = (entity.primerefs.PrimeRefsDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_PrimeRefs = null;
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
		// no attributes
		return outgoing;
	}
	
	public void clearOutgoing() {
		clearOutgoing(false);
	}
	
	public void clearOutgoing(boolean delete) {
		// no attributes
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
		// no attributes
	}
	
	public boolean removeOutgoing(PanacheEntity e) {
		return removeOutgoing(e, false);
	}
	
	public boolean removeOutgoing(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		// no attributes
		return false;
	}
	
	public boolean containsOutgoing(PanacheEntity e) {
		// containment-check of the entities type-specific list
		// no attributes
		return false;
	}
	
	public boolean isEmptyOutgoing() {
		return getOutgoing().isEmpty();
	}
	
	public int sizeOutgoing() {
		return getOutgoing().size();
	}
	
	public PanacheEntity getPr() {
		if(pr_Activity != null) {
			return pr_Activity;
		}
		return null;
	}
	
	public void setPr(PanacheEntity e) {
		setPr(e, false);
	}
	
	public void setPr(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(pr_Activity != null && !pr_Activity.equals(e)) {
				pr_Activity.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.ActivityDB) {
			// null all other types
			// set element
			pr_Activity = (entity.flowgraph.ActivityDB) e;
			return;
		}
		
		// default-case
		// null all types
		pr_Activity = null;
	}
	
	@Override
	public void delete() {
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.primerefs.PrimeRefsDB) {
			entity.primerefs.PrimeRefsDB container = (entity.primerefs.PrimeRefsDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// delete entity
		super.delete();
	}
}
