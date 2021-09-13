package info.scce.pyro.externallibrary.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.externallibrary.rest.ExternalActivity")
public class ExternalActivity implements info.scce.pyro.core.graphmodel.IdentifiableElement 
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
	
	private String name;
							
	@com.fasterxml.jackson.annotation.JsonProperty("name")
	public String getname() {
	    return this.name;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("name")
	public void setName(final String name) {
	    this.name = name;
	}
	
	private String description;
							
	@com.fasterxml.jackson.annotation.JsonProperty("description")
	public String getdescription() {
	    return this.description;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("description")
	public void setDescription(final String description) {
	    this.description = description;
	}
	
	public static ExternalActivity fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache o) {
		info.scce.pyro.rest.ObjectCache objectCache = o;
		if(objectCache == null)
			objectCache = new info.scce.pyro.rest.ObjectCache();
		
		return fromEntity(dbEntity,objectCache);
	}
	
	public static ExternalActivity fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.externallibrary.ExternalActivityDB) {
			entity.externallibrary.ExternalActivityDB entity = (entity.externallibrary.ExternalActivityDB) dbEntity;
			if(objectCache!=null && objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final ExternalActivity result;
			result = new ExternalActivity();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type("externallibrary.ExternalActivity");
			
			result.setName(entity.name);
			result.setDescription(entity.description);
			return result;
		}
		return null;
	}
}
