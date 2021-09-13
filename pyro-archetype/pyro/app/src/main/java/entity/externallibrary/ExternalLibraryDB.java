package entity.externallibrary;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_externallibrary_externallibrary")
public class ExternalLibraryDB extends PanacheEntity {
	
	public String filename;
	
	public String extension;
	
	public String name;
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalactivitylibrary_externalactivitylibrary",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivitylibrarydb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityLibraryDB> ExternalActivityLibrary_ExternalActivityLibrary = new java.util.ArrayList<>();
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externallibrary_externalactivity_externalactivity",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externallibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivitydb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityDB> ExternalActivity_ExternalActivity = new java.util.ArrayList<>();
	
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
	
	public java.util.Collection<PanacheEntity> getExternalActivity() {
		java.util.Collection<PanacheEntity> ExternalActivity = new java.util.ArrayList<>();
		ExternalActivity.addAll(ExternalActivity_ExternalActivity);
		return ExternalActivity;
	}
	
	public void clearExternalActivity() {
		clearExternalActivity(false);
	}
	
	public void clearExternalActivity(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityDB> iter_ExternalActivity_ExternalActivity = ExternalActivity_ExternalActivity.iterator();
			while(iter_ExternalActivity_ExternalActivity.hasNext()) {
				entity.externallibrary.ExternalActivityDB e = iter_ExternalActivity_ExternalActivity.next();
				if(e != null) {
					e.delete();
					ExternalActivity_ExternalActivity.remove(e);
				}
				iter_ExternalActivity_ExternalActivity = ExternalActivity_ExternalActivity.iterator();
			}
		} else {
			// clear all collections
			ExternalActivity_ExternalActivity.clear();
		}
	}
	
	public void setExternalActivity(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearExternalActivity();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addExternalActivity(e);
		}
	}
	
	public void addAllExternalActivity(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addExternalActivity(e);
		}
	}
	
	public void addExternalActivity(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			ExternalActivity_ExternalActivity.add((entity.externallibrary.ExternalActivityDB) e);
		}
	}
	
	public boolean removeExternalActivity(PanacheEntity e) {
		return removeExternalActivity(e, false);
	}
	
	public boolean removeExternalActivity(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			entity.externallibrary.ExternalActivityDB definitiveEntity = (entity.externallibrary.ExternalActivityDB) e;
			if(ExternalActivity_ExternalActivity.contains(definitiveEntity)) {
				boolean result = ExternalActivity_ExternalActivity.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsExternalActivity(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			entity.externallibrary.ExternalActivityDB definitiveEntity = (entity.externallibrary.ExternalActivityDB) e;
			return ExternalActivity_ExternalActivity.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyExternalActivity() {
		return getExternalActivity().isEmpty();
	}
	
	public int sizeExternalActivity() {
		return getExternalActivity().size();
	}
	
	@Override
	public void delete() {
		// cleanup all contained EClassifier
		this.clearExternalActivityLibrary(true);
		this.clearExternalActivity(true);
		
		// delete entity
		super.delete();
	}
}
