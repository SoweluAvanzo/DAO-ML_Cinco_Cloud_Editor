package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.EdgeB")
public abstract class EdgeB extends EdgeC
{
	private String ofB;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofB")
	public String getofB() {
	    return this.ofB;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofB")
	public void setofB(final String ofB) {
	    this.ofB = ofB;
	}
	
	private TB tb;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_tb")
	public TB gettb() {
	    return this.tb;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("tb")
	public void settb(final TB tb) {
	    this.tb = tb;
	}
	
	private java.util.List<TB> tbList;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_tbList")
	public java.util.List<TB> gettbList() {
	    return this.tbList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("tbList")
	public void settbList(final java.util.List<TB> tbList) {
	    this.tbList = tbList;
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
	
	private info.scce.pyro.core.graphmodel.IdentifiableElement container;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_container")
	public info.scce.pyro.core.graphmodel.IdentifiableElement getcontainer() {
		return this.container;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("container")
	public void setcontainer(final info.scce.pyro.core.graphmodel.IdentifiableElement container) {
		this.container = container;
	}
	private info.scce.pyro.core.graphmodel.Node sourceElement;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_sourceElement")
	public info.scce.pyro.core.graphmodel.Node getsourceElement() {
	    return this.sourceElement;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("sourceElement")
	public void setsourceElement(final info.scce.pyro.core.graphmodel.Node sourceElement) {
	    this.sourceElement = sourceElement;
	}
	
	private info.scce.pyro.core.graphmodel.Node targetElement;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_targetElement")
	public info.scce.pyro.core.graphmodel.Node gettargetElement() {
	    return this.targetElement;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("targetElement")
	public void settargetElement(final info.scce.pyro.core.graphmodel.Node targetElement) {
	    this.targetElement = targetElement;
	}
	
	private java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> bendingPoints;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_bendingPoints")
	public java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> getbendingPoints() {
	    return this.bendingPoints;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("bendingPoints")
	public void setbendingPoints(final java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> bendingPoints) {
	    this.bendingPoints = bendingPoints;
	}
	
		
	public static EdgeB fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			return info.scce.pyro.hierarchy.rest.EdgeA.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static EdgeB fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			return info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

