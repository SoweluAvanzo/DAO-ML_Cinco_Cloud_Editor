package entity.externallibrary;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_externallibrary_externalactivitylibrary")
public class ExternalActivityLibraryDB extends PanacheEntity {
	
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
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_activities_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> activities_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_representsa_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> representsA_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_representsb_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> representsB_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_representsc_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> representsC_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_representsd_externalactivityd",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityddb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityDDB> representsD_ExternalActivityD = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_representsd_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> representsD_ExternalActivityA = new java.util.ArrayList<>();
	
	public java.util.Collection<PanacheEntity> getActivities() {
		java.util.Collection<PanacheEntity> activities = new java.util.ArrayList<>();
		activities.addAll(activities_ExternalActivityA);
		return activities;
	}
	
	public void clearActivities() {
		clearActivities(false);
	}
	
	public void clearActivities(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_activities_ExternalActivityA = activities_ExternalActivityA.iterator();
			while(iter_activities_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_activities_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					activities_ExternalActivityA.remove(e);
				}
				iter_activities_ExternalActivityA = activities_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			activities_ExternalActivityA.clear();
		}
	}
	
	public void setActivities(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearActivities();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addActivities(e);
		}
	}
	
	public void addAllActivities(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addActivities(e);
		}
	}
	
	public void addActivities(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			activities_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeActivities(PanacheEntity e) {
		return removeActivities(e, false);
	}
	
	public boolean removeActivities(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(activities_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = activities_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsActivities(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return activities_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyActivities() {
		return getActivities().isEmpty();
	}
	
	public int sizeActivities() {
		return getActivities().size();
	}
	
	public java.util.Collection<PanacheEntity> getRepresentsA() {
		java.util.Collection<PanacheEntity> representsA = new java.util.ArrayList<>();
		representsA.addAll(representsA_ExternalActivityA);
		return representsA;
	}
	
	public void clearRepresentsA() {
		clearRepresentsA(false);
	}
	
	public void clearRepresentsA(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_representsA_ExternalActivityA = representsA_ExternalActivityA.iterator();
			while(iter_representsA_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_representsA_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					representsA_ExternalActivityA.remove(e);
				}
				iter_representsA_ExternalActivityA = representsA_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			representsA_ExternalActivityA.clear();
		}
	}
	
	public void setRepresentsA(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearRepresentsA();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addRepresentsA(e);
		}
	}
	
	public void addAllRepresentsA(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addRepresentsA(e);
		}
	}
	
	public void addRepresentsA(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			representsA_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeRepresentsA(PanacheEntity e) {
		return removeRepresentsA(e, false);
	}
	
	public boolean removeRepresentsA(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(representsA_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = representsA_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsRepresentsA(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return representsA_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyRepresentsA() {
		return getRepresentsA().isEmpty();
	}
	
	public int sizeRepresentsA() {
		return getRepresentsA().size();
	}
	
	public java.util.Collection<PanacheEntity> getRepresentsB() {
		java.util.Collection<PanacheEntity> representsB = new java.util.ArrayList<>();
		representsB.addAll(representsB_ExternalActivityA);
		return representsB;
	}
	
	public void clearRepresentsB() {
		clearRepresentsB(false);
	}
	
	public void clearRepresentsB(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_representsB_ExternalActivityA = representsB_ExternalActivityA.iterator();
			while(iter_representsB_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_representsB_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					representsB_ExternalActivityA.remove(e);
				}
				iter_representsB_ExternalActivityA = representsB_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			representsB_ExternalActivityA.clear();
		}
	}
	
	public void setRepresentsB(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearRepresentsB();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addRepresentsB(e);
		}
	}
	
	public void addAllRepresentsB(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addRepresentsB(e);
		}
	}
	
	public void addRepresentsB(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			representsB_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeRepresentsB(PanacheEntity e) {
		return removeRepresentsB(e, false);
	}
	
	public boolean removeRepresentsB(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(representsB_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = representsB_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsRepresentsB(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return representsB_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyRepresentsB() {
		return getRepresentsB().isEmpty();
	}
	
	public int sizeRepresentsB() {
		return getRepresentsB().size();
	}
	
	public java.util.Collection<PanacheEntity> getRepresentsC() {
		java.util.Collection<PanacheEntity> representsC = new java.util.ArrayList<>();
		representsC.addAll(representsC_ExternalActivityA);
		return representsC;
	}
	
	public void clearRepresentsC() {
		clearRepresentsC(false);
	}
	
	public void clearRepresentsC(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_representsC_ExternalActivityA = representsC_ExternalActivityA.iterator();
			while(iter_representsC_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_representsC_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					representsC_ExternalActivityA.remove(e);
				}
				iter_representsC_ExternalActivityA = representsC_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			representsC_ExternalActivityA.clear();
		}
	}
	
	public void setRepresentsC(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearRepresentsC();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addRepresentsC(e);
		}
	}
	
	public void addAllRepresentsC(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addRepresentsC(e);
		}
	}
	
	public void addRepresentsC(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			representsC_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeRepresentsC(PanacheEntity e) {
		return removeRepresentsC(e, false);
	}
	
	public boolean removeRepresentsC(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(representsC_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = representsC_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsRepresentsC(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return representsC_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyRepresentsC() {
		return getRepresentsC().isEmpty();
	}
	
	public int sizeRepresentsC() {
		return getRepresentsC().size();
	}
	
	public java.util.Collection<PanacheEntity> getRepresentsD() {
		java.util.Collection<PanacheEntity> representsD = new java.util.ArrayList<>();
		representsD.addAll(representsD_ExternalActivityD);
		representsD.addAll(representsD_ExternalActivityA);
		return representsD;
	}
	
	public void clearRepresentsD() {
		clearRepresentsD(false);
	}
	
	public void clearRepresentsD(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityDDB> iter_representsD_ExternalActivityD = representsD_ExternalActivityD.iterator();
			while(iter_representsD_ExternalActivityD.hasNext()) {
				entity.externallibrary.ExternalActivityDDB e = iter_representsD_ExternalActivityD.next();
				if(e != null) {
					e.delete();
					representsD_ExternalActivityD.remove(e);
				}
				iter_representsD_ExternalActivityD = representsD_ExternalActivityD.iterator();
			}
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_representsD_ExternalActivityA = representsD_ExternalActivityA.iterator();
			while(iter_representsD_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_representsD_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					representsD_ExternalActivityA.remove(e);
				}
				iter_representsD_ExternalActivityA = representsD_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			representsD_ExternalActivityD.clear();
			representsD_ExternalActivityA.clear();
		}
	}
	
	public void setRepresentsD(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearRepresentsD();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addRepresentsD(e);
		}
	}
	
	public void addAllRepresentsD(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addRepresentsD(e);
		}
	}
	
	public void addRepresentsD(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			representsD_ExternalActivityD.add((entity.externallibrary.ExternalActivityDDB) e);
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			representsD_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeRepresentsD(PanacheEntity e) {
		return removeRepresentsD(e, false);
	}
	
	public boolean removeRepresentsD(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB definitiveEntity = (entity.externallibrary.ExternalActivityDDB) e;
			if(representsD_ExternalActivityD.contains(definitiveEntity)) {
				boolean result = representsD_ExternalActivityD.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(representsD_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = representsD_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsRepresentsD(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB definitiveEntity = (entity.externallibrary.ExternalActivityDDB) e;
			return representsD_ExternalActivityD.contains(definitiveEntity);
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return representsD_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyRepresentsD() {
		return getRepresentsD().isEmpty();
	}
	
	public int sizeRepresentsD() {
		return getRepresentsD().size();
	}
	
	@Override
	public void delete() {
		this.valueintegerlist.clear();
		this.valuelonglist.clear();
		this.valuestringlist.clear();
		this.clearActivities(true);
		this.clearRepresentsA(true);
		this.clearRepresentsB(true);
		this.clearRepresentsC(true);
		this.clearRepresentsD(true);
		
		// delete entity
		super.delete();
	}
}
