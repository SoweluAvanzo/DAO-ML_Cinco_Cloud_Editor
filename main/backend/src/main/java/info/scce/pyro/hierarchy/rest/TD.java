package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.TD")
public class TD implements info.scce.pyro.core.graphmodel.IdentifiableElement
{
	private String ofTD;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofTD")
	public String getofTD() {
	    return this.ofTD;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofTD")
	public void setofTD(final String ofTD) {
	    this.ofTD = ofTD;
	}
	
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
		return id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}
		
	public static TD fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.TDDB) {
			entity.hierarchy.TDDB entity = (entity.hierarchy.TDDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final TD result;
			result = new TD();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setofTD(entity.ofTD);
			
			
			return result;
		}
		// delegating to subTypes
		else if(dbEntity instanceof entity.hierarchy.TADB) {
			return info.scce.pyro.hierarchy.rest.TA.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static TD fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.TDDB) {
			entity.hierarchy.TDDB entity = (entity.hierarchy.TDDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final TD result;
			result = new TD();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setofTD(entity.ofTD);
			
			
			return result;
		}
		// delegating to subTypes
		else if(dbEntity instanceof entity.hierarchy.TADB) {
			return info.scce.pyro.hierarchy.rest.TA.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

