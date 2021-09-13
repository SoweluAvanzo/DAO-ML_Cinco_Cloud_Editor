package entity.externallibrary;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_externallibrary_externalactivitylibrary")
public class ExternalActivityLibraryDB extends PanacheEntity {
	
	@javax.persistence.JoinTable(
		name = "entity_externallibrary_externalactivitylibrary_activities_externalactivity",
		joinColumns = { @javax.persistence.JoinColumn(name = "parent_externalactivitylibrarydb_id") },
		inverseJoinColumns = { @javax.persistence.JoinColumn(name = "child_externalactivitydb_id") }
	)
	@javax.persistence.OneToMany
	public java.util.Collection<entity.externallibrary.ExternalActivityDB> activities_ExternalActivity = new java.util.ArrayList<>();
	
	public java.util.Collection<PanacheEntity> getActivities() {
		java.util.Collection<PanacheEntity> activities = new java.util.ArrayList<>();
		activities.addAll(activities_ExternalActivity);
		return activities;
	}
	
	public void clearActivities() {
		clearActivities(false);
	}
	
	public void clearActivities(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.externallibrary.ExternalActivityDB> iter_activities_ExternalActivity = activities_ExternalActivity.iterator();
			while(iter_activities_ExternalActivity.hasNext()) {
				entity.externallibrary.ExternalActivityDB e = iter_activities_ExternalActivity.next();
				if(e != null) {
					e.delete();
					activities_ExternalActivity.remove(e);
				}
				iter_activities_ExternalActivity = activities_ExternalActivity.iterator();
			}
		} else {
			// clear all collections
			activities_ExternalActivity.clear();
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
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			activities_ExternalActivity.add((entity.externallibrary.ExternalActivityDB) e);
		}
	}
	
	public boolean removeActivities(PanacheEntity e) {
		return removeActivities(e, false);
	}
	
	public boolean removeActivities(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			entity.externallibrary.ExternalActivityDB definitiveEntity = (entity.externallibrary.ExternalActivityDB) e;
			if(activities_ExternalActivity.contains(definitiveEntity)) {
				boolean result = activities_ExternalActivity.remove(definitiveEntity);
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
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			entity.externallibrary.ExternalActivityDB definitiveEntity = (entity.externallibrary.ExternalActivityDB) e;
			return activities_ExternalActivity.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyActivities() {
		return getActivities().isEmpty();
	}
	
	public int sizeActivities() {
		return getActivities().size();
	}
	
	@Override
	public void delete() {
		this.clearActivities(true);
		
		// delete entity
		super.delete();
	}
}
