package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.TB")
public abstract class TB extends TC
{
	private String ofTB;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofTB")
	public String getofTB() {
	    return this.ofTB;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofTB")
	public void setofTB(final String ofTB) {
	    this.ofTB = ofTB;
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
		
	public static TB fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.TADB) {
			return info.scce.pyro.hierarchy.rest.TA.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static TB fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.TADB) {
			return info.scce.pyro.hierarchy.rest.TA.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

