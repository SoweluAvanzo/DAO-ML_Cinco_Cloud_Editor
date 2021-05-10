package entity.primerefs;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_primerefs_sourceedge")
public class SourceEdgeDB extends PanacheEntity {
	
	@javax.persistence.ManyToOne
	public entity.primerefs.PrimeRefsDB container_PrimeRefs;
	
	@javax.persistence.OneToMany
	public java.util.Collection<entity.core.BendingPointDB> bendingPoints = new java.util.ArrayList<>();
	
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
	
	public PanacheEntity getSource() {
		return null;
	}
	
	public void setSource(PanacheEntity e) {
		setSource(e, false);
	}
	
	public void setSource(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
		}
		
		// set new and null others
		
		// default-case
		// null all types
	}
	
	public PanacheEntity getTarget() {
		return null;
	}
	
	public void setTarget(PanacheEntity e) {
		setTarget(e, false);
	}
	
	public void setTarget(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
		}
		
		// set new and null others
		
		// default-case
		// null all types
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
		
		// remove bendingPoints
		for(entity.core.BendingPointDB b : bendingPoints) {
			b.delete();
		}
		bendingPoints.clear();
		
		// delete entity
		super.delete();
	}
}
