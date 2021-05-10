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
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> activities;
									
	@com.fasterxml.jackson.annotation.JsonProperty("activities")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> getactivities() {
	    return this.activities;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("activities")
	public void setActivities(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> activities) {
	    this.activities = activities;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> representsA;
									
	@com.fasterxml.jackson.annotation.JsonProperty("representsA")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> getrepresentsA() {
	    return this.representsA;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("representsA")
	public void setRepresentsA(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> representsA) {
	    this.representsA = representsA;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> representsB;
									
	@com.fasterxml.jackson.annotation.JsonProperty("representsB")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> getrepresentsB() {
	    return this.representsB;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("representsB")
	public void setRepresentsB(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> representsB) {
	    this.representsB = representsB;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC> representsC;
									
	@com.fasterxml.jackson.annotation.JsonProperty("representsC")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC> getrepresentsC() {
	    return this.representsC;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("representsC")
	public void setRepresentsC(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC> representsC) {
	    this.representsC = representsC;
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> representsD;
									
	@com.fasterxml.jackson.annotation.JsonProperty("representsD")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> getrepresentsD() {
	    return this.representsD;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("representsD")
	public void setRepresentsD(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> representsD) {
	    this.representsD = representsD;
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
			
			result.setName(entity.name);
			result.setValueInteger(entity.valueinteger);
			result.setValueLong(entity.valuelong);
			result.setValueString(entity.valuestring);
			result.setValueIntegerList(entity.valueintegerlist);
			result.setValueLongList(entity.valuelonglist);
			result.setValueStringList(entity.valuestringlist);
			result.setActivities(
				entity.getActivities().stream().map((n)->
					info.scce.pyro.externallibrary.rest.ExternalActivityA.fromEntity(
						n,
						objectCache
					)
				).collect(java.util.stream.Collectors.toList())
			);
			result.setRepresentsA(
				entity.getRepresentsA().stream().map((n)->
					info.scce.pyro.externallibrary.rest.ExternalActivityA.fromEntity(
						n,
						objectCache
					)
				).collect(java.util.stream.Collectors.toList())
			);
			result.setRepresentsB(
				entity.getRepresentsB().stream().map((n)->
					info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB.fromEntity(
						n,
						objectCache
					)
				).collect(java.util.stream.Collectors.toList())
			);
			result.setRepresentsC(
				entity.getRepresentsC().stream().map((n)->
					info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC.fromEntity(
						n,
						objectCache
					)
				).collect(java.util.stream.Collectors.toList())
			);
			result.setRepresentsD(
				entity.getRepresentsD().stream().map((n)->
					info.scce.pyro.externallibrary.rest.ExternalActivityD.fromEntity(
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
