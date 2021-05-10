package entity.hierarchy;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hierarchy_edgea")
public class EdgeADB extends PanacheEntity {
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.HierarchyDB container_Hierarchy;
	
	@javax.persistence.OneToMany
	public java.util.Collection<entity.core.BendingPointDB> bendingPoints = new java.util.ArrayList<>();
	
	public String ofA;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TADB ta_TA;
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_edgea_talist_ta",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_edgeadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TADB> taList_TA = new java.util.ArrayList<>();
	
	public String ofB;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TADB tb_TA;
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_edgea_tblist_ta",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_edgeadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TADB> tbList_TA = new java.util.ArrayList<>();
	
	public String ofC;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TADB tc_TA;
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_edgea_tclist_ta",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_edgeadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TADB> tcList_TA = new java.util.ArrayList<>();
	
	public String ofD;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TDDB td_TD;
	
	@javax.persistence.ManyToOne
	public entity.hierarchy.TADB td_TA;
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_edgea_tdlist_td",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_edgeadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tddb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TDDB> tdList_TD = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_hierarchy_edgea_tdlist_ta",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_edgeadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_tadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.hierarchy.TADB> tdList_TA = new java.util.ArrayList<>();
	
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
	
	public java.util.Collection<PanacheEntity> getTaList() {
		java.util.Collection<PanacheEntity> taList = new java.util.ArrayList<>();	
		taList.addAll(taList_TA);
		return taList;
	}
	
	public void clearTaList() {
		clearTaList(false);
	}
	
	public void clearTaList(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hierarchy.TADB> iter_taList_TA = taList_TA.iterator();
			while(iter_taList_TA.hasNext()) {
				entity.hierarchy.TADB e = iter_taList_TA.next();
				if(e != null) {
					e.delete();
					taList_TA.remove(e);
				}
				iter_taList_TA = taList_TA.iterator();
			}
		} else {
			// clear all collections
			taList_TA.clear();
		}
	}
	
	public void setTaList(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearTaList();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addTaList(e);
		}
	}
	
	public void addAllTaList(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addTaList(e);
		}
	}
	
	public void addTaList(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			taList_TA.add((entity.hierarchy.TADB) e);
		}
	}
	
	public boolean removeTaList(PanacheEntity e) {
		return removeTaList(e, false);
	}
	
	public boolean removeTaList(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			if(taList_TA.contains(definitiveEntity)) {
				boolean result = taList_TA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsTaList(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			return taList_TA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyTaList() {
		return getTaList().isEmpty();
	}
	
	public int sizeTaList() {
		return getTaList().size();
	}
	
	public PanacheEntity getTb() {
		if(tb_TA != null) {
			return tb_TA;
		}
		return null;
	}
	
	public void setTb(PanacheEntity e) {
		setTb(e, false);
	}
	
	public void setTb(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(tb_TA != null && !tb_TA.equals(e)) {
				tb_TA.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hierarchy.TADB) {
			// null all other types
			// set element
			tb_TA = (entity.hierarchy.TADB) e;
			return;
		}
		
		// default-case
		// null all types
		tb_TA = null;
	}
	
	public java.util.Collection<PanacheEntity> getTbList() {
		java.util.Collection<PanacheEntity> tbList = new java.util.ArrayList<>();	
		tbList.addAll(tbList_TA);
		return tbList;
	}
	
	public void clearTbList() {
		clearTbList(false);
	}
	
	public void clearTbList(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hierarchy.TADB> iter_tbList_TA = tbList_TA.iterator();
			while(iter_tbList_TA.hasNext()) {
				entity.hierarchy.TADB e = iter_tbList_TA.next();
				if(e != null) {
					e.delete();
					tbList_TA.remove(e);
				}
				iter_tbList_TA = tbList_TA.iterator();
			}
		} else {
			// clear all collections
			tbList_TA.clear();
		}
	}
	
	public void setTbList(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearTbList();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addTbList(e);
		}
	}
	
	public void addAllTbList(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addTbList(e);
		}
	}
	
	public void addTbList(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			tbList_TA.add((entity.hierarchy.TADB) e);
		}
	}
	
	public boolean removeTbList(PanacheEntity e) {
		return removeTbList(e, false);
	}
	
	public boolean removeTbList(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			if(tbList_TA.contains(definitiveEntity)) {
				boolean result = tbList_TA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsTbList(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			return tbList_TA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyTbList() {
		return getTbList().isEmpty();
	}
	
	public int sizeTbList() {
		return getTbList().size();
	}
	
	public PanacheEntity getTc() {
		if(tc_TA != null) {
			return tc_TA;
		}
		return null;
	}
	
	public void setTc(PanacheEntity e) {
		setTc(e, false);
	}
	
	public void setTc(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(tc_TA != null && !tc_TA.equals(e)) {
				tc_TA.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.hierarchy.TADB) {
			// null all other types
			// set element
			tc_TA = (entity.hierarchy.TADB) e;
			return;
		}
		
		// default-case
		// null all types
		tc_TA = null;
	}
	
	public java.util.Collection<PanacheEntity> getTcList() {
		java.util.Collection<PanacheEntity> tcList = new java.util.ArrayList<>();	
		tcList.addAll(tcList_TA);
		return tcList;
	}
	
	public void clearTcList() {
		clearTcList(false);
	}
	
	public void clearTcList(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.hierarchy.TADB> iter_tcList_TA = tcList_TA.iterator();
			while(iter_tcList_TA.hasNext()) {
				entity.hierarchy.TADB e = iter_tcList_TA.next();
				if(e != null) {
					e.delete();
					tcList_TA.remove(e);
				}
				iter_tcList_TA = tcList_TA.iterator();
			}
		} else {
			// clear all collections
			tcList_TA.clear();
		}
	}
	
	public void setTcList(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearTcList();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addTcList(e);
		}
	}
	
	public void addAllTcList(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addTcList(e);
		}
	}
	
	public void addTcList(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			tcList_TA.add((entity.hierarchy.TADB) e);
		}
	}
	
	public boolean removeTcList(PanacheEntity e) {
		return removeTcList(e, false);
	}
	
	public boolean removeTcList(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			if(tcList_TA.contains(definitiveEntity)) {
				boolean result = tcList_TA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsTcList(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB definitiveEntity = (entity.hierarchy.TADB) e;
			return tcList_TA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyTcList() {
		return getTcList().isEmpty();
	}
	
	public int sizeTcList() {
		return getTcList().size();
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
		// remove bendingPoints
		for(entity.core.BendingPointDB b : bendingPoints) {
			b.delete();
		}
		bendingPoints.clear();
		
		// cleanup all complex-attributes
		this.setTa(null, true);
		this.clearTaList(true);
		this.setTb(null, true);
		this.clearTbList(true);
		this.setTc(null, true);
		this.clearTcList(true);
		this.setTd(null, true);
		this.clearTdList(true);
		
		// delete entity
		super.delete();
	}
}
