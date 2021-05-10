package info.scce.pyro.externallibrary.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC")
public class ExternalAbstractActivityC extends info.scce.pyro.externallibrary.rest.ExternalActivityD 
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
	
	private long ofD;
							
	@com.fasterxml.jackson.annotation.JsonProperty("ofD")
	public long getofD() {
	    return this.ofD;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofD")
	public void setOfD(final long ofD) {
	    this.ofD = ofD;
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
	
	private long valueInteger;
							
	@com.fasterxml.jackson.annotation.JsonProperty("valueInteger")
	public long getvalueInteger() {
	    return this.valueInteger;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("valueInteger")
	public void setValueInteger(final long valueInteger) {
	    this.valueInteger = valueInteger;
	}
	
	private long valueLong;
							
	@com.fasterxml.jackson.annotation.JsonProperty("valueLong")
	public long getvalueLong() {
	    return this.valueLong;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("valueLong")
	public void setValueLong(final long valueLong) {
	    this.valueLong = valueLong;
	}
	
	private String valueString;
							
	@com.fasterxml.jackson.annotation.JsonProperty("valueString")
	public String getvalueString() {
	    return this.valueString;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("valueString")
	public void setValueString(final String valueString) {
	    this.valueString = valueString;
	}
	
	private java.util.Collection<Long> valueIntegerList;
							
	@com.fasterxml.jackson.annotation.JsonProperty("valueIntegerList")
	public java.util.Collection<Long> getvalueIntegerList() {
	    return this.valueIntegerList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("valueIntegerList")
	public void setValueIntegerList(final java.util.Collection<Long> valueIntegerList) {
	    this.valueIntegerList = valueIntegerList;
	}
	
	private java.util.Collection<Long> valueLongList;
							
	@com.fasterxml.jackson.annotation.JsonProperty("valueLongList")
	public java.util.Collection<Long> getvalueLongList() {
	    return this.valueLongList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("valueLongList")
	public void setValueLongList(final java.util.Collection<Long> valueLongList) {
	    this.valueLongList = valueLongList;
	}
	
	private java.util.Collection<String> valueStringList;
							
	@com.fasterxml.jackson.annotation.JsonProperty("valueStringList")
	public java.util.Collection<String> getvalueStringList() {
	    return this.valueStringList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("valueStringList")
	public void setValueStringList(final java.util.Collection<String> valueStringList) {
	    this.valueStringList = valueStringList;
	}
	
	private String ofC;
							
	@com.fasterxml.jackson.annotation.JsonProperty("ofC")
	public String getofC() {
	    return this.ofC;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofC")
	public void setOfC(final String ofC) {
	    this.ofC = ofC;
	}
	
	private info.scce.pyro.externallibrary.rest.ExternalActivityD referencedOfD;
									
	@com.fasterxml.jackson.annotation.JsonProperty("referencedOfD")
	public info.scce.pyro.externallibrary.rest.ExternalActivityD getreferencedOfD() {
	    return this.referencedOfD;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("referencedOfD")
	public void setReferencedOfD(final info.scce.pyro.externallibrary.rest.ExternalActivityD referencedOfD) {
	    this.referencedOfD = referencedOfD;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> recerencingAbstractList;
									
	@com.fasterxml.jackson.annotation.JsonProperty("recerencingAbstractList")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> getrecerencingAbstractList() {
	    return this.recerencingAbstractList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("recerencingAbstractList")
	public void setRecerencingAbstractList(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> recerencingAbstractList) {
	    this.recerencingAbstractList = recerencingAbstractList;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> referencingList;
									
	@com.fasterxml.jackson.annotation.JsonProperty("referencingList")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> getreferencingList() {
	    return this.referencingList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("referencingList")
	public void setReferencingList(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> referencingList) {
	    this.referencingList = referencingList;
	}
	
	public static ExternalAbstractActivityC fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache o) {
		info.scce.pyro.rest.ObjectCache objectCache = o;
		if(objectCache == null)
			objectCache = new info.scce.pyro.rest.ObjectCache();
		
		return fromEntity(dbEntity,objectCache);
	}
	
	public static ExternalAbstractActivityC fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.externallibrary.ExternalActivityADB) {
			return info.scce.pyro.externallibrary.rest.ExternalActivityA.fromEntity(dbEntity, objectCache);
		}
		return null;
	}
}
