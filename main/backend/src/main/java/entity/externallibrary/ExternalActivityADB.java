package entity.externallibrary;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_externallibrary_externalactivitya")
public class ExternalActivityADB extends PanacheEntity {
	
	public String ofa;
	
	public String description;
	
	public String ofb;
	
	public String ofc;
	
	public long ofd;
	
	public String name;
	
	public long valueinteger;
	
	public long valuelong;
	
	public String valuestring;
	
	@javax.persistence.ElementCollection
	public java.util.Collection<Long> valueintegerlist = new java.util.ArrayList<>();
	
	@javax.persistence.ElementCollection
	public java.util.Collection<Long> valuelonglist = new java.util.ArrayList<>();
	
	@javax.persistence.ElementCollection
	public java.util.Collection<String> valuestringlist = new java.util.ArrayList<>();
	
	@javax.persistence.ManyToOne
	public entity.externallibrary.ExternalActivityDDB referencedOfD_ExternalActivityD;
	
	@javax.persistence.ManyToOne
	public entity.externallibrary.ExternalActivityADB referencedOfD_ExternalActivityA;
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitya_recerencingabstractlist_externalactivityd",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivityadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityddb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityDDB> recerencingAbstractList_ExternalActivityD = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitya_recerencingabstractlist_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivityadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> recerencingAbstractList_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitya_referencinglist_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivityadb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> referencingList_ExternalActivityA = new java.util.ArrayList<>();
	
	public PanacheEntity getReferencedOfD() {
		if(referencedOfD_ExternalActivityD != null) {
			return referencedOfD_ExternalActivityD;
		} else if(referencedOfD_ExternalActivityA != null) {
			return referencedOfD_ExternalActivityA;
		}
		return null;
	}
	
	public void setReferencedOfD(PanacheEntity e) {
		setReferencedOfD(e, false);
	}
	
	public void setReferencedOfD(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			if(referencedOfD_ExternalActivityD != null) {
				referencedOfD_ExternalActivityD.delete();
			}
			if(referencedOfD_ExternalActivityA != null) {
				referencedOfD_ExternalActivityA.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			// null all other types
			referencedOfD_ExternalActivityA = null;
			// set element
			referencedOfD_ExternalActivityD = (entity.externallibrary.ExternalActivityDDB) e;
			return;
		} else if(e instanceof entity.externallibrary.ExternalActivityADB) {
			// null all other types
			referencedOfD_ExternalActivityD = null;
			// set element
			referencedOfD_ExternalActivityA = (entity.externallibrary.ExternalActivityADB) e;
			return;
		}
		
		// default-case
		// null all types
		referencedOfD_ExternalActivityD = null;
		referencedOfD_ExternalActivityA = null;
	}
	
	public java.util.Collection<PanacheEntity> getRecerencingAbstractList() {
		java.util.Collection<PanacheEntity> recerencingAbstractList = new java.util.ArrayList<>();
		recerencingAbstractList.addAll(recerencingAbstractList_ExternalActivityD);
		recerencingAbstractList.addAll(recerencingAbstractList_ExternalActivityA);
		return recerencingAbstractList;
	}
	
	public void clearRecerencingAbstractList() {
		clearRecerencingAbstractList(false);
	}
	
	public void clearRecerencingAbstractList(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityDDB> iter_recerencingAbstractList_ExternalActivityD = recerencingAbstractList_ExternalActivityD.iterator();
			while(iter_recerencingAbstractList_ExternalActivityD.hasNext()) {
				entity.externallibrary.ExternalActivityDDB e = iter_recerencingAbstractList_ExternalActivityD.next();
				if(e != null) {
					e.delete();
					recerencingAbstractList_ExternalActivityD.remove(e);
				}
				iter_recerencingAbstractList_ExternalActivityD = recerencingAbstractList_ExternalActivityD.iterator();
			}
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_recerencingAbstractList_ExternalActivityA = recerencingAbstractList_ExternalActivityA.iterator();
			while(iter_recerencingAbstractList_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_recerencingAbstractList_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					recerencingAbstractList_ExternalActivityA.remove(e);
				}
				iter_recerencingAbstractList_ExternalActivityA = recerencingAbstractList_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			recerencingAbstractList_ExternalActivityD.clear();
			recerencingAbstractList_ExternalActivityA.clear();
		}
	}
	
	public void setRecerencingAbstractList(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearRecerencingAbstractList();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addRecerencingAbstractList(e);
		}
	}
	
	public void addAllRecerencingAbstractList(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addRecerencingAbstractList(e);
		}
	}
	
	public void addRecerencingAbstractList(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			recerencingAbstractList_ExternalActivityD.add((entity.externallibrary.ExternalActivityDDB) e);
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			recerencingAbstractList_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeRecerencingAbstractList(PanacheEntity e) {
		return removeRecerencingAbstractList(e, false);
	}
	
	public boolean removeRecerencingAbstractList(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB definitiveEntity = (entity.externallibrary.ExternalActivityDDB) e;
			if(recerencingAbstractList_ExternalActivityD.contains(definitiveEntity)) {
				boolean result = recerencingAbstractList_ExternalActivityD.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(recerencingAbstractList_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = recerencingAbstractList_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsRecerencingAbstractList(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB definitiveEntity = (entity.externallibrary.ExternalActivityDDB) e;
			return recerencingAbstractList_ExternalActivityD.contains(definitiveEntity);
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return recerencingAbstractList_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyRecerencingAbstractList() {
		return getRecerencingAbstractList().isEmpty();
	}
	
	public int sizeRecerencingAbstractList() {
		return getRecerencingAbstractList().size();
	}
	
	public java.util.Collection<PanacheEntity> getReferencingList() {
		java.util.Collection<PanacheEntity> referencingList = new java.util.ArrayList<>();
		referencingList.addAll(referencingList_ExternalActivityA);
		return referencingList;
	}
	
	public void clearReferencingList() {
		clearReferencingList(false);
	}
	
	public void clearReferencingList(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_referencingList_ExternalActivityA = referencingList_ExternalActivityA.iterator();
			while(iter_referencingList_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_referencingList_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					referencingList_ExternalActivityA.remove(e);
				}
				iter_referencingList_ExternalActivityA = referencingList_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			referencingList_ExternalActivityA.clear();
		}
	}
	
	public void setReferencingList(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearReferencingList();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addReferencingList(e);
		}
	}
	
	public void addAllReferencingList(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addReferencingList(e);
		}
	}
	
	public void addReferencingList(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			referencingList_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeReferencingList(PanacheEntity e) {
		return removeReferencingList(e, false);
	}
	
	public boolean removeReferencingList(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(referencingList_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = referencingList_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsReferencingList(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return referencingList_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyReferencingList() {
		return getReferencingList().isEmpty();
	}
	
	public int sizeReferencingList() {
		return getReferencingList().size();
	}
	
	@Override
	public void delete() {
		this.valueintegerlist.clear();
		this.valuelonglist.clear();
		this.valuestringlist.clear();
		this.setReferencedOfD(null, true);
		this.clearRecerencingAbstractList(true);
		this.clearReferencingList(true);
		
		// delete entity
		super.delete();
	}
}
