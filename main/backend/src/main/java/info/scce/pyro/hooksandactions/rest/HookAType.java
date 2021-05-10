package info.scce.pyro.hooksandactions.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hooksandactions.rest.HookAType")
public class HookAType implements info.scce.pyro.core.graphmodel.IdentifiableElement
{
	private String attribute;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_attribute")
	public String getattribute() {
	    return this.attribute;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("attribute")
	public void setattribute(final String attribute) {
	    this.attribute = attribute;
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
		
	public static HookAType fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hooksandactions.HookATypeDB) {
			entity.hooksandactions.HookATypeDB entity = (entity.hooksandactions.HookATypeDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final HookAType result;
			result = new HookAType();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setattribute(entity.attribute);
			
			
			return result;
		}
		else
			return null;
	}
	
	public static HookAType fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hooksandactions.HookATypeDB) {
			entity.hooksandactions.HookATypeDB entity = (entity.hooksandactions.HookATypeDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final HookAType result;
			result = new HookAType();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setattribute(entity.attribute);
			
			
			return result;
		}
		else
			return null;
	}
}

