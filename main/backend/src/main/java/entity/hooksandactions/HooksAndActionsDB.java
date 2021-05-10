package entity.hooksandactions;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hooksandactions_hooksandactions")
public class HooksAndActionsDB extends PanacheEntity {
	
	public String router;
	
	public String connector;
	
	public long width;
	
	public long height;
	
	public double scale;
	
	public boolean isPublic;
	
	public String filename;
	
	public String extension;
	
	@javax.persistence.ManyToOne
	public entity.core.PyroFileContainerDB parent;
	
	@javax.persistence.OneToMany(mappedBy="container_HooksAndActions")
	public java.util.Collection<entity.hooksandactions.HookANodeDB> modelElements_HookANode = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_HooksAndActions")
	public java.util.Collection<entity.hooksandactions.HookAContainerDB> modelElements_HookAContainer = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_HooksAndActions")
	public java.util.Collection<entity.hooksandactions.HookAnEdgeDB> modelElements_HookAnEdge = new java.util.ArrayList<>();
	
	@javax.persistence.ManyToOne
	public entity.hooksandactions.HookATypeDB atype_HookAType;
	
	public String attribute;
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		modelElements.addAll(modelElements_HookANode);
		modelElements.addAll(modelElements_HookAContainer);
		modelElements.addAll(modelElements_HookAnEdge);
		return modelElements;
	}
	
	public void clearModelElements() {
		clearModelElements(false);
	}
	
	public void clearModelElements(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hooksandactions.HookANodeDB> iter_modelElements_HookANode = modelElements_HookANode.iterator();
			while(iter_modelElements_HookANode.hasNext()) {
				entity.hooksandactions.HookANodeDB e = iter_modelElements_HookANode.next();
				if(e != null) {
					e.delete();
					modelElements_HookANode.remove(e);
				}
				iter_modelElements_HookANode = modelElements_HookANode.iterator();
			}
			java.util.Iterator<entity.hooksandactions.HookAContainerDB> iter_modelElements_HookAContainer = modelElements_HookAContainer.iterator();
			while(iter_modelElements_HookAContainer.hasNext()) {
				entity.hooksandactions.HookAContainerDB e = iter_modelElements_HookAContainer.next();
				if(e != null) {
					e.delete();
					modelElements_HookAContainer.remove(e);
				}
				iter_modelElements_HookAContainer = modelElements_HookAContainer.iterator();
			}
			java.util.Iterator<entity.hooksandactions.HookAnEdgeDB> iter_modelElements_HookAnEdge = modelElements_HookAnEdge.iterator();
			while(iter_modelElements_HookAnEdge.hasNext()) {
				entity.hooksandactions.HookAnEdgeDB e = iter_modelElements_HookAnEdge.next();
				if(e != null) {
					e.delete();
					modelElements_HookAnEdge.remove(e);
				}
				iter_modelElements_HookAnEdge = modelElements_HookAnEdge.iterator();
			}
		} else {
			// clear all collections
			modelElements_HookANode.clear();
			modelElements_HookAContainer.clear();
			modelElements_HookAnEdge.clear();
		}
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
		if(e instanceof entity.hooksandactions.HookANodeDB) {
			modelElements_HookANode.add((entity.hooksandactions.HookANodeDB) e);
		} else 
		if(e instanceof entity.hooksandactions.HookAContainerDB) {
			modelElements_HookAContainer.add((entity.hooksandactions.HookAContainerDB) e);
		} else 
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			modelElements_HookAnEdge.add((entity.hooksandactions.HookAnEdgeDB) e);
		}
	}
	
	public boolean removeModelElements(PanacheEntity e) {
		return removeModelElements(e, false);
	}
	
	public boolean removeModelElements(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB definitiveEntity = (entity.hooksandactions.HookANodeDB) e;
			if(modelElements_HookANode.contains(definitiveEntity)) {
				boolean result = modelElements_HookANode.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB definitiveEntity = (entity.hooksandactions.HookAContainerDB) e;
			if(modelElements_HookAContainer.contains(definitiveEntity)) {
				boolean result = modelElements_HookAContainer.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB definitiveEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			if(modelElements_HookAnEdge.contains(definitiveEntity)) {
				boolean result = modelElements_HookAnEdge.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsModelElements(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.hooksandactions.HookANodeDB) {
			entity.hooksandactions.HookANodeDB definitiveEntity = (entity.hooksandactions.HookANodeDB) e;
			return modelElements_HookANode.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hooksandactions.HookAContainerDB) {
			entity.hooksandactions.HookAContainerDB definitiveEntity = (entity.hooksandactions.HookAContainerDB) e;
			return modelElements_HookAContainer.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
			entity.hooksandactions.HookAnEdgeDB definitiveEntity = (entity.hooksandactions.HookAnEdgeDB) e;
			return modelElements_HookAnEdge.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyModelElements() {
		return getModelElements().isEmpty();
	}
	
	public int sizeModelElements() {
		return getModelElements().size();
	}
	
	public PanacheEntity getAtype() {
		if(atype_HookAType != null) {
			return atype_HookAType;
		}
		return null;
	}
	
	public void setAtype(PanacheEntity e) {
		setAtype(e, false);
	}
	
	public void setAtype(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(atype_HookAType != null && !atype_HookAType.equals(e)) {
				atype_HookAType.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hooksandactions.HookATypeDB) {
			// null all other types
			// set element
			atype_HookAType = (entity.hooksandactions.HookATypeDB) e;
			return;
		}
		
		// default-case
		// null all types
		atype_HookAType = null;
	}
	
	@Override
	public void delete() {
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// cleanup all complex-attributes
		this.setAtype(null, true);
		
		// delete entity
		super.delete();
	}
}
