package entity.hooksandactions;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hooksandactions_hookanedge")
public class HookAnEdgeDB extends PanacheEntity {
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HooksAndActionsDB container_HooksAndActions;
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HookANodeDB source_HookANode;
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HookAContainerDB source_HookAContainer;
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HookANodeDB target_HookANode;
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HookAContainerDB target_HookAContainer;
	
	@javax.persistence.OneToMany
	public java.util.Collection<entity.core.BendingPointDB> bendingPoints = new java.util.ArrayList<>();
	
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
	
	public PanacheEntity getSource() {
		if(source_HookANode != null) {
			return source_HookANode;
		} else if(source_HookAContainer != null) {
			return source_HookAContainer;
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
			if(source_HookANode != null && !source_HookANode.equals(e)) {
				source_HookANode.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(source_HookAContainer != null && !source_HookAContainer.equals(e)) {
				source_HookAContainer.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hooksandactions.HookANodeDB) {
			// null all other types
			source_HookAContainer = null;
			// set element
			source_HookANode = (entity.hooksandactions.HookANodeDB) e;
			return;
		} else if(e instanceof entity.hooksandactions.HookAContainerDB) {
			// null all other types
			source_HookANode = null;
			// set element
			source_HookAContainer = (entity.hooksandactions.HookAContainerDB) e;
			return;
		}
		
		// default-case
		// null all types
		source_HookANode = null;
		source_HookAContainer = null;
	}
	
	public PanacheEntity getTarget() {
		if(target_HookANode != null) {
			return target_HookANode;
		} else if(target_HookAContainer != null) {
			return target_HookAContainer;
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
			if(target_HookANode != null && !target_HookANode.equals(e)) {
				target_HookANode.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(target_HookAContainer != null && !target_HookAContainer.equals(e)) {
				target_HookAContainer.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hooksandactions.HookANodeDB) {
			// null all other types
			target_HookAContainer = null;
			// set element
			target_HookANode = (entity.hooksandactions.HookANodeDB) e;
			return;
		} else if(e instanceof entity.hooksandactions.HookAContainerDB) {
			// null all other types
			target_HookANode = null;
			// set element
			target_HookAContainer = (entity.hooksandactions.HookAContainerDB) e;
			return;
		}
		
		// default-case
		// null all types
		target_HookANode = null;
		target_HookAContainer = null;
	}
	
	@Override
	public void delete() {
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.hooksandactions.HooksAndActionsDB) {
			entity.hooksandactions.HooksAndActionsDB container = (entity.hooksandactions.HooksAndActionsDB) c;
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
		if(dbSource instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB source = (entity.hooksandactions.HookANodeDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		if(dbSource instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB source = (entity.hooksandactions.HookAContainerDB) dbSource;
			source.removeOutgoing(this);
			source.persist();
		}
		this.setSource(null);
		
		// decouple from target
		PanacheEntity dbTarget = this.getTarget();
		if(dbTarget instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB target = (entity.hooksandactions.HookANodeDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		if(dbTarget instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB target = (entity.hooksandactions.HookAContainerDB) dbTarget;
			target.removeIncoming(this);
			target.persist();
		}
		this.setTarget(null);
		
		// delete entity
		super.delete();
	}
}
