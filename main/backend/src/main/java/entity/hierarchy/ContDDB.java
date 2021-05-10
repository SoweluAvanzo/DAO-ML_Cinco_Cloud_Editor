package entity.hierarchy;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hierarchy_contd")
public class ContDDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.HierarchyDB container_Hierarchy;
	
	public String ofContD;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TDDB td_TD;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TADB td_TA;
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_contd_tdlist_td",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_contddb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tddb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TDDB> tdList_TD = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_contd_tdlist_ta",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_contddb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TADB> tdList_TA = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_ContD")
	public java.util.Collection<entity.hierarchy.DDB> modelElements_D = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_ContD")
	public java.util.Collection<entity.hierarchy.ADB> modelElements_A = new java.util.ArrayList<>();
	
	public PanacheEntity getContainer() {
		if(container_Hierarchy != null) {
			return container_Hierarchy;
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
			if(container_Hierarchy != null && !container_Hierarchy.equals(e)) {
				container_Hierarchy.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hierarchy.HierarchyDB) {
			// null all other types
			// set element
			container_Hierarchy = (entity.hierarchy.HierarchyDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_Hierarchy = null;
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
	
	public PanacheEntity getTd() {
		if(td_TD != null) {
			return td_TD;
		} else if(td_TA != null) {
			return td_TA;
		}
		return null;
	}
	
	public void setTd(PanacheEntity e) {
		setTd(e, false);
	}
	
	public void setTd(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(td_TD != null && !td_TD.equals(e)) {
				td_TD.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(td_TA != null && !td_TA.equals(e)) {
				td_TA.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hierarchy.TDDB) {
			// null all other types
			td_TA = null;
			// set element
			td_TD = (entity.hierarchy.TDDB) e;
			return;
		} else if(e instanceof entity.hierarchy.TADB) {
			// null all other types
			td_TD = null;
			// set element
			td_TA = (entity.hierarchy.TADB) e;
			return;
		}
		
		// default-case
		// null all types
		td_TD = null;
		td_TA = null;
	}
	
	public java.util.Collection<PanacheEntity> getTdList() {
		java.util.Collection<PanacheEntity> tdList = new java.util.ArrayList<>();	
		tdList.addAll(tdList_TD);
		tdList.addAll(tdList_TA);
		return tdList;
	}
	
	public void clearTdList() {
		clearTdList(false);
	}
	
	public void clearTdList(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hierarchy.TDDB> iter_tdList_TD = tdList_TD.iterator();
			while(iter_tdList_TD.hasNext()) {
				entity.hierarchy.TDDB e = iter_tdList_TD.next();
				if(e != null) {
					e.delete();
					tdList_TD.remove(e);
				}
				iter_tdList_TD = tdList_TD.iterator();
			}
			java.util.Iterator<entity.hierarchy.TADB> iter_tdList_TA = tdList_TA.iterator();
			while(iter_tdList_TA.hasNext()) {
				entity.hierarchy.TADB e = iter_tdList_TA.next();
				if(e != null) {
					e.delete();
					tdList_TA.remove(e);
				}
				iter_tdList_TA = tdList_TA.iterator();
			}
		} else {
			// clear all collections
			tdList_TD.clear();
			tdList_TA.clear();
		}
	}
	
	public void setTdList(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearTdList();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addTdList(e);
		}
	}
	
	public void addAllTdList(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addTdList(e);
		}
	}
	
	public void addTdList(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.hierarchy.TDDB) {
			tdList_TD.add((entity.hierarchy.TDDB) e);
		} else 
		if(e instanceof entity.hierarchy.TADB) {
			tdList_TA.add((entity.hierarchy.TADB) e);
		}
	}
	
	public boolean removeTdList(PanacheEntity e) {
		return removeTdList(e, false);
	}
	
	public boolean removeTdList(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hierarchy.TDDB) {
			entity.hierarchy.TDDB definitiveEntity = (entity.hierarchy.TDDB) e;
			if(tdList_TD.contains(definitiveEntity)) {
				boolean result = tdList_TD.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			if(tdList_TA.contains(definitiveEntity)) {
				boolean result = tdList_TA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsTdList(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.hierarchy.TDDB) {
			entity.hierarchy.TDDB definitiveEntity = (entity.hierarchy.TDDB) e;
			return tdList_TD.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			return tdList_TA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyTdList() {
		return getTdList().isEmpty();
	}
	
	public int sizeTdList() {
		return getTdList().size();
	}
	
	@Override
	public void delete() {
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.hierarchy.HierarchyDB) {
			entity.hierarchy.HierarchyDB container = (entity.hierarchy.HierarchyDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// cleanup all complex-attributes
		this.setTd(null, true);
		this.clearTdList(true);
		
		// delete entity
		super.delete();
	}
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		modelElements.addAll(modelElements_D);
		modelElements.addAll(modelElements_A);
		return modelElements;
	}
	
	public void clearModelElements() {
		clearModelElements(false);
	}
	
	public void clearModelElements(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hierarchy.DDB> iter_modelElements_D = modelElements_D.iterator();
			while(iter_modelElements_D.hasNext()) {
				entity.hierarchy.DDB e = iter_modelElements_D.next();
				if(e != null) {
					e.delete();
					modelElements_D.remove(e);
				}
				iter_modelElements_D = modelElements_D.iterator();
			}
			java.util.Iterator<entity.hierarchy.ADB> iter_modelElements_A = modelElements_A.iterator();
			while(iter_modelElements_A.hasNext()) {
				entity.hierarchy.ADB e = iter_modelElements_A.next();
				if(e != null) {
					e.delete();
					modelElements_A.remove(e);
				}
				iter_modelElements_A = modelElements_A.iterator();
			}
		} else {
			// clear all collections
			modelElements_D.clear();
			modelElements_A.clear();
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
		if(e instanceof entity.hierarchy.DDB) {
			modelElements_D.add((entity.hierarchy.DDB) e);
		} else 
		if(e instanceof entity.hierarchy.ADB) {
			modelElements_A.add((entity.hierarchy.ADB) e);
		}
	}
	
	public boolean removeModelElements(PanacheEntity e) {
		return removeModelElements(e, false);
	}
	
	public boolean removeModelElements(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hierarchy.DDB) {
			entity.hierarchy.DDB definitiveEntity = (entity.hierarchy.DDB) e;
			if(modelElements_D.contains(definitiveEntity)) {
				boolean result = modelElements_D.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.hierarchy.ADB) {
			entity.hierarchy.ADB definitiveEntity = (entity.hierarchy.ADB) e;
			if(modelElements_A.contains(definitiveEntity)) {
				boolean result = modelElements_A.remove(definitiveEntity);
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
		if(e instanceof entity.hierarchy.DDB) {
			entity.hierarchy.DDB definitiveEntity = (entity.hierarchy.DDB) e;
			return modelElements_D.contains(definitiveEntity);
		} else 
		if(e instanceof entity.hierarchy.ADB) {
			entity.hierarchy.ADB definitiveEntity = (entity.hierarchy.ADB) e;
			return modelElements_A.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyModelElements() {
		return getModelElements().isEmpty();
	}
	
	public int sizeModelElements() {
		return getModelElements().size();
	}
}
