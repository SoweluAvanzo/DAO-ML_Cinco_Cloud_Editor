package info.scce.pyro.externallibrary.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.externallibrary.rest.ExternalActivityLibrary")
public class ExternalActivityLibrary implements info.scce.pyro.core.graphmodel.IdentifiableElement 
{
	private String __type;
					
    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public String get__type() {
        return this.__type;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public void set__type(final String __type) {
        this.__type = __type;
    }
    
    @com.fasterxml.jackson.annotation.JsonProperty(info.scce.pyro.util.Constants.PYRO_ID)
    private long id;
    
    @Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivity> activities;
									
	@com.fasterxml.jackson.annotation.JsonProperty("activities")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivity> getactivities() {
	    return this.activities;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("activities")
	public void setActivities(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivity> activities) {
	    this.activities = activities;
	}
	
	public static ExternalActivityLibrary fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache o) {
		info.scce.pyro.rest.ObjectCache objectCache = o;
		if(objectCache == null)
			objectCache = new info.scce.pyro.rest.ObjectCache();
		
		return fromEntity(dbEntity,objectCache);
	}
	
	public static ExternalActivityLibrary fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.externallibrary.ExternalActivityLibraryDB) {
			entity.externallibrary.ExternalActivityLibraryDB entity = (entity.externallibrary.ExternalActivityLibraryDB) dbEntity;
			if(objectCache!=null && objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final ExternalActivityLibrary result;
			result = new ExternalActivityLibrary();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type("externallibrary.ExternalActivityLibrary");
			
			result.setActivities(
				entity.getActivities().stream().map((n)->
					info.scce.pyro.externallibrary.rest.ExternalActivity.fromEntity(
						n,
						objectCache
					)
				).collect(java.util.stream.Collectors.toList())
			);
			return result;
		}
		return null;
	}
}
