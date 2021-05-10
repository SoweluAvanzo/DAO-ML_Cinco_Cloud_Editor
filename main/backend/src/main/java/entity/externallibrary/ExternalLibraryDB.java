package entity.externallibrary;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_externallibrary_externallibrary")
public class ExternalLibraryDB extends PanacheEntity {
	
	public String filename;
	
	public String extension;
	
	@javax.persistence.ManyToOne
	public entity.core.PyroFileContainerDB parent;
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalactivitylibrary_externalactivitylibrary",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivitylibrarydb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityLibraryDB> ExternalActivityLibrary_ExternalActivityLibrary = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalactivitya_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> ExternalActivityA_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalabstractactivityb_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> ExternalAbstractActivityB_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalabstractactivityc_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> ExternalAbstractActivityC_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalactivityd_externalactivityd",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityddb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityDDB> ExternalActivityD_ExternalActivityD = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalactivityd_externalactivitya",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivityadb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityADB> ExternalActivityD_ExternalActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
	@javax.persistence.ElementCollection
	public java.util.Collection<entity.externallibrary.ExternalEnumDB> externalenum = new java.util.ArrayList<>();
	
	public java.util.Collection<PanacheEntity> getExternalActivityLibrary() {
		java.util.Collection<PanacheEntity> ExternalActivityLibrary = new java.util.ArrayList<>();
		ExternalActivityLibrary.addAll(ExternalActivityLibrary_ExternalActivityLibrary);
		return ExternalActivityLibrary;
	}
	
	public void clearExternalActivityLibrary() {
		clearExternalActivityLibrary(false);
	}
	
	public void clearExternalActivityLibrary(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityLibraryDB> iter_ExternalActivityLibrary_ExternalActivityLibrary = ExternalActivityLibrary_ExternalActivityLibrary.iterator();
			while(iter_ExternalActivityLibrary_ExternalActivityLibrary.hasNext()) {
				entity.externallibrary.ExternalActivityLibraryDB e = iter_ExternalActivityLibrary_ExternalActivityLibrary.next();
				if(e != null) {
					e.delete();
					ExternalActivityLibrary_ExternalActivityLibrary.remove(e);
				}
				iter_ExternalActivityLibrary_ExternalActivityLibrary = ExternalActivityLibrary_ExternalActivityLibrary.iterator();
			}
		} else {
			// clear all collections
			ExternalActivityLibrary_ExternalActivityLibrary.clear();
		}
	}
	
	public void setExternalActivityLibrary(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearExternalActivityLibrary();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addExternalActivityLibrary(e);
		}
	}
	
	public void addAllExternalActivityLibrary(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addExternalActivityLibrary(e);
		}
	}
	
	public void addExternalActivityLibrary(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityLibraryDB) {
			ExternalActivityLibrary_ExternalActivityLibrary.add((entity.externallibrary.ExternalActivityLibraryDB) e);
		}
	}
	
	public boolean removeExternalActivityLibrary(PanacheEntity e) {
		return removeExternalActivityLibrary(e, false);
	}
	
	public boolean removeExternalActivityLibrary(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityLibraryDB) {
			entity.externallibrary.ExternalActivityLibraryDB definitiveEntity = (entity.externallibrary.ExternalActivityLibraryDB) e;
			if(ExternalActivityLibrary_ExternalActivityLibrary.contains(definitiveEntity)) {
				boolean result = ExternalActivityLibrary_ExternalActivityLibrary.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsExternalActivityLibrary(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityLibraryDB) {
			entity.externallibrary.ExternalActivityLibraryDB definitiveEntity = (entity.externallibrary.ExternalActivityLibraryDB) e;
			return ExternalActivityLibrary_ExternalActivityLibrary.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyExternalActivityLibrary() {
		return getExternalActivityLibrary().isEmpty();
	}
	
	public int sizeExternalActivityLibrary() {
		return getExternalActivityLibrary().size();
	}
	
	public java.util.Collection<PanacheEntity> getExternalActivityA() {
		java.util.Collection<PanacheEntity> ExternalActivityA = new java.util.ArrayList<>();
		ExternalActivityA.addAll(ExternalActivityA_ExternalActivityA);
		return ExternalActivityA;
	}
	
	public void clearExternalActivityA() {
		clearExternalActivityA(false);
	}
	
	public void clearExternalActivityA(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_ExternalActivityA_ExternalActivityA = ExternalActivityA_ExternalActivityA.iterator();
			while(iter_ExternalActivityA_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_ExternalActivityA_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					ExternalActivityA_ExternalActivityA.remove(e);
				}
				iter_ExternalActivityA_ExternalActivityA = ExternalActivityA_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			ExternalActivityA_ExternalActivityA.clear();
		}
	}
	
	public void setExternalActivityA(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearExternalActivityA();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addExternalActivityA(e);
		}
	}
	
	public void addAllExternalActivityA(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addExternalActivityA(e);
		}
	}
	
	public void addExternalActivityA(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			ExternalActivityA_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeExternalActivityA(PanacheEntity e) {
		return removeExternalActivityA(e, false);
	}
	
	public boolean removeExternalActivityA(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(ExternalActivityA_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = ExternalActivityA_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsExternalActivityA(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return ExternalActivityA_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyExternalActivityA() {
		return getExternalActivityA().isEmpty();
	}
	
	public int sizeExternalActivityA() {
		return getExternalActivityA().size();
	}
	
	public java.util.Collection<PanacheEntity> getExternalAbstractActivityB() {
		java.util.Collection<PanacheEntity> ExternalAbstractActivityB = new java.util.ArrayList<>();
		ExternalAbstractActivityB.addAll(ExternalAbstractActivityB_ExternalActivityA);
		return ExternalAbstractActivityB;
	}
	
	public void clearExternalAbstractActivityB() {
		clearExternalAbstractActivityB(false);
	}
	
	public void clearExternalAbstractActivityB(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_ExternalAbstractActivityB_ExternalActivityA = ExternalAbstractActivityB_ExternalActivityA.iterator();
			while(iter_ExternalAbstractActivityB_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_ExternalAbstractActivityB_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					ExternalAbstractActivityB_ExternalActivityA.remove(e);
				}
				iter_ExternalAbstractActivityB_ExternalActivityA = ExternalAbstractActivityB_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			ExternalAbstractActivityB_ExternalActivityA.clear();
		}
	}
	
	public void setExternalAbstractActivityB(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearExternalAbstractActivityB();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addExternalAbstractActivityB(e);
		}
	}
	
	public void addAllExternalAbstractActivityB(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addExternalAbstractActivityB(e);
		}
	}
	
	public void addExternalAbstractActivityB(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			ExternalAbstractActivityB_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeExternalAbstractActivityB(PanacheEntity e) {
		return removeExternalAbstractActivityB(e, false);
	}
	
	public boolean removeExternalAbstractActivityB(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(ExternalAbstractActivityB_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = ExternalAbstractActivityB_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsExternalAbstractActivityB(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return ExternalAbstractActivityB_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyExternalAbstractActivityB() {
		return getExternalAbstractActivityB().isEmpty();
	}
	
	public int sizeExternalAbstractActivityB() {
		return getExternalAbstractActivityB().size();
	}
	
	public java.util.Collection<PanacheEntity> getExternalAbstractActivityC() {
		java.util.Collection<PanacheEntity> ExternalAbstractActivityC = new java.util.ArrayList<>();
		ExternalAbstractActivityC.addAll(ExternalAbstractActivityC_ExternalActivityA);
		return ExternalAbstractActivityC;
	}
	
	public void clearExternalAbstractActivityC() {
		clearExternalAbstractActivityC(false);
	}
	
	public void clearExternalAbstractActivityC(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_ExternalAbstractActivityC_ExternalActivityA = ExternalAbstractActivityC_ExternalActivityA.iterator();
			while(iter_ExternalAbstractActivityC_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_ExternalAbstractActivityC_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					ExternalAbstractActivityC_ExternalActivityA.remove(e);
				}
				iter_ExternalAbstractActivityC_ExternalActivityA = ExternalAbstractActivityC_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			ExternalAbstractActivityC_ExternalActivityA.clear();
		}
	}
	
	public void setExternalAbstractActivityC(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearExternalAbstractActivityC();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addExternalAbstractActivityC(e);
		}
	}
	
	public void addAllExternalAbstractActivityC(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addExternalAbstractActivityC(e);
		}
	}
	
	public void addExternalAbstractActivityC(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			ExternalAbstractActivityC_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeExternalAbstractActivityC(PanacheEntity e) {
		return removeExternalAbstractActivityC(e, false);
	}
	
	public boolean removeExternalAbstractActivityC(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(ExternalAbstractActivityC_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = ExternalAbstractActivityC_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsExternalAbstractActivityC(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return ExternalAbstractActivityC_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyExternalAbstractActivityC() {
		return getExternalAbstractActivityC().isEmpty();
	}
	
	public int sizeExternalAbstractActivityC() {
		return getExternalAbstractActivityC().size();
	}
	
	public java.util.Collection<PanacheEntity> getExternalActivityD() {
		java.util.Collection<PanacheEntity> ExternalActivityD = new java.util.ArrayList<>();
		ExternalActivityD.addAll(ExternalActivityD_ExternalActivityD);
		ExternalActivityD.addAll(ExternalActivityD_ExternalActivityA);
		return ExternalActivityD;
	}
	
	public void clearExternalActivityD() {
		clearExternalActivityD(false);
	}
	
	public void clearExternalActivityD(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityDDB> iter_ExternalActivityD_ExternalActivityD = ExternalActivityD_ExternalActivityD.iterator();
			while(iter_ExternalActivityD_ExternalActivityD.hasNext()) {
				entity.externallibrary.ExternalActivityDDB e = iter_ExternalActivityD_ExternalActivityD.next();
				if(e != null) {
					e.delete();
					ExternalActivityD_ExternalActivityD.remove(e);
				}
				iter_ExternalActivityD_ExternalActivityD = ExternalActivityD_ExternalActivityD.iterator();
			}
			java.util.Iterator<entity.externallibrary.ExternalActivityADB> iter_ExternalActivityD_ExternalActivityA = ExternalActivityD_ExternalActivityA.iterator();
			while(iter_ExternalActivityD_ExternalActivityA.hasNext()) {
				entity.externallibrary.ExternalActivityADB e = iter_ExternalActivityD_ExternalActivityA.next();
				if(e != null) {
					e.delete();
					ExternalActivityD_ExternalActivityA.remove(e);
				}
				iter_ExternalActivityD_ExternalActivityA = ExternalActivityD_ExternalActivityA.iterator();
			}
		} else {
			// clear all collections
			ExternalActivityD_ExternalActivityD.clear();
			ExternalActivityD_ExternalActivityA.clear();
		}
	}
	
	public void setExternalActivityD(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearExternalActivityD();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addExternalActivityD(e);
		}
	}
	
	public void addAllExternalActivityD(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addExternalActivityD(e);
		}
	}
	
	public void addExternalActivityD(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			ExternalActivityD_ExternalActivityD.add((entity.externallibrary.ExternalActivityDDB) e);
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			ExternalActivityD_ExternalActivityA.add((entity.externallibrary.ExternalActivityADB) e);
		}
	}
	
	public boolean removeExternalActivityD(PanacheEntity e) {
		return removeExternalActivityD(e, false);
	}
	
	public boolean removeExternalActivityD(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB definitiveEntity = (entity.externallibrary.ExternalActivityDDB) e;
			if(ExternalActivityD_ExternalActivityD.contains(definitiveEntity)) {
				boolean result = ExternalActivityD_ExternalActivityD.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			if(ExternalActivityD_ExternalActivityA.contains(definitiveEntity)) {
				boolean result = ExternalActivityD_ExternalActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsExternalActivityD(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB definitiveEntity = (entity.externallibrary.ExternalActivityDDB) e;
			return ExternalActivityD_ExternalActivityD.contains(definitiveEntity);
		} else 
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB definitiveEntity = (entity.externallibrary.ExternalActivityADB) e;
			return ExternalActivityD_ExternalActivityA.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyExternalActivityD() {
		return getExternalActivityD().isEmpty();
	}
	
	public int sizeExternalActivityD() {
		return getExternalActivityD().size();
	}
	
	@Override
	public void delete() {
		// cleanup all contained EClassifier
		this.clearExternalActivityLibrary(true);
		this.clearExternalActivityA(true);
		this.clearExternalAbstractActivityB(true);
		this.clearExternalAbstractActivityC(true);
		this.clearExternalActivityD(true);
		
		// delete entity
		super.delete();
	}
}
