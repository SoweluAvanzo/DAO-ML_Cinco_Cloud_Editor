package entity.hierarchy;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hierarchy_hierarchy")
public class HierarchyDB extends PanacheEntity {
	
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
	
	@javax.persistence.OneToMany(mappedBy="container_Hierarchy")
	public java.util.Collection<entity.hierarchy.ContADB> modelElements_ContA = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Hierarchy")
	public java.util.Collection<entity.hierarchy.ContDB> modelElements_Cont = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Hierarchy")
	public java.util.Collection<entity.hierarchy.EdgeADB> modelElements_EdgeA = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Hierarchy")
	public java.util.Collection<entity.hierarchy.EdgeDDB> modelElements_EdgeD = new java.util.ArrayList<>();
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TADB ta_TA;
	
	public String modelName;
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		modelElements.addAll(modelElements_ContA);
		modelElements.addAll(modelElements_Cont);
		modelElements.addAll(modelElements_EdgeA);
		modelElements.addAll(modelElements_EdgeD);
		return modelElements;
	}
	
	public void clearModelElements() {
		clearModelElements(false);
	}
	
	public void clearModelElements(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hierarchy.ContADB> iter_modelElements_ContA = modelElements_ContA.iterator();
			while(iter_modelElements_ContA.hasNext()) {
				entity.hierarchy.ContADB e = iter_modelElements_ContA.next();
				if(e != null) {
					e.delete();
					modelElements_ContA.remove(e);
				}
				iter_modelElements_ContA = modelElements_ContA.iterator();
			}
			java.util.Iterator<entity.hierarchy.ContDB> iter_modelElements_Cont = modelElements_Cont.iterator();
			while(iter_modelElements_Cont.hasNext()) {
				entity.hierarchy.ContDB e = iter_modelElements_Cont.next();
				if(e != null) {
					e.delete();
					modelElements_Cont.remove(e);
				}
				iter_modelElements_Cont = modelElements_Cont.iterator();
			}
			java.util.Iterator<entity.hierarchy.EdgeADB> iter_modelElements_EdgeA = modelElements_EdgeA.iterator();
			while(iter_modelElements_EdgeA.hasNext()) {
				entity.hierarchy.EdgeADB e = iter_modelElements_EdgeA.next();
				if(e != null) {
					e.delete();
					modelElements_EdgeA.remove(e);
				}
				iter_modelElements_EdgeA = modelElements_EdgeA.iterator();
			}
			java.util.Iterator<entity.hierarchy.EdgeDDB> iter_modelElements_EdgeD = modelElements_EdgeD.iterator();
			while(iter_modelElements_EdgeD.hasNext()) {
				entity.hierarchy.EdgeDDB e = iter_modelElements_EdgeD.next();
				if(e != null) {
					e.delete();
					modelElements_EdgeD.remove(e);
				}
				iter_modelElements_EdgeD = modelElements_EdgeD.iterator();
			}
		} else {
			// clear all collections
			modelElements_ContA.clear();
			modelElements_Cont.clear();
			modelElements_EdgeA.clear();
			modelElements_EdgeD.clear();
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
		if(e instanceof entity.hierarchy.ContADB) {
			modelElements_ContA.add((entity.hierarchy.ContADB) e);
		} else 
		if(e instanceof entity.hierarchy.ContDB) {
			modelElements_Cont.add((entity.hierarchy.ContDB) e);
		} else 
		if(e instanceof entity.hierarchy.EdgeADB) {
			modelElements_EdgeA.add((entity.hierarchy.EdgeADB) e);
		} else 
		if(e instanceof entity.hierarchy.EdgeDDB) {
			modelElements_EdgeD.add((entity.hierarchy.EdgeDDB) e);
		}
	}
	
	public boolean removeModelElements(PanacheEntity e) {
		return removeModelElements(e, false);
	}
	
	public boolean removeModelElements(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hierarchy.ContADB) {
			entity.hierarchy.ContADB definitiveEntity = (entity.hierarchy.ContADB) e;
			if(modelElements_ContA.contains(definitiveEntity)) {
				boolean result = modelElements_ContA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hierarchy.ContDB) {
			entity.hierarchy.ContDB definitiveEntity = (entity.hierarchy.ContDB) e;
			if(modelElements_Cont.contains(definitiveEntity)) {
				boolean result = modelElements_Cont.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hierarchy.EdgeADB) {
			entity.hierarchy.EdgeADB definitiveEntity = (entity.hierarchy.EdgeADB) e;
			if(modelElements_EdgeA.contains(definitiveEntity)) {
				boolean result = modelElements_EdgeA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hierarchy.EdgeDDB) {
			entity.hierarchy.EdgeDDB definitiveEntity = (entity.hierarchy.EdgeDDB) e;
			if(modelElements_EdgeD.contains(definitiveEntity)) {
				boolean result = modelElements_EdgeD.remove(definitiveEntity);
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
		if(e instanceof entity.hierarchy.ContADB) {
			entity.hierarchy.ContADB definitiveEntity = (entity.hierarchy.ContADB) e;
			return modelElements_ContA.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hierarchy.ContDB) {
			entity.hierarchy.ContDB definitiveEntity = (entity.hierarchy.ContDB) e;
			return modelElements_Cont.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hierarchy.EdgeADB) {
			entity.hierarchy.EdgeADB definitiveEntity = (entity.hierarchy.EdgeADB) e;
			return modelElements_EdgeA.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hierarchy.EdgeDDB) {
			entity.hierarchy.EdgeDDB definitiveEntity = (entity.hierarchy.EdgeDDB) e;
			return modelElements_EdgeD.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyModelElements() {
		return getModelElements().isEmpty();
	}
	
	public int sizeModelElements() {
		return getModelElements().size();
	}
	
	public PanacheEntity getTa() {
		if(ta_TA != null) {
			return ta_TA;
		}
		return null;
	}
	
	public void setTa(PanacheEntity e) {
		setTa(e, false);
	}
	
	public void setTa(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(ta_TA != null && !ta_TA.equals(e)) {
				ta_TA.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hierarchy.TADB) {
			// null all other types
			// set element
			ta_TA = (entity.hierarchy.TADB) e;
			return;
		}
		
		// default-case
		// null all types
		ta_TA = null;
	}
	
	@Override
	public void delete() {
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// cleanup all complex-attributes
		this.setTa(null, true);
		
		// delete entity
		super.delete();
	}
}
