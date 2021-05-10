package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.TA")
public class TA extends TB
{
	private String ofTA;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofTA")
	public String getofTA() {
	    return this.ofTA;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofTA")
	public void setofTA(final String ofTA) {
	    this.ofTA = ofTA;
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
		
	public static TA fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB entity = (entity.hierarchy.TADB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final TA result;
			result = new TA();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setofTA(entity.ofTA);
			
			
			
			result.setofTB(entity.ofTB);
			
			
			
			result.setofTC(entity.ofTC);
			
			
			
			result.setofTD(entity.ofTD);
			
			
			return result;
		}
		else
			return null;
	}
	
	public static TA fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.TADB) {
			entity.hierarchy.TADB entity = (entity.hierarchy.TADB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final TA result;
			result = new TA();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setofTA(entity.ofTA);
			
			
			
			result.setofTB(entity.ofTB);
			
			
			
			result.setofTC(entity.ofTC);
			
			
			
			result.setofTD(entity.ofTD);
			
			
			return result;
		}
		else
			return null;
	}
}

