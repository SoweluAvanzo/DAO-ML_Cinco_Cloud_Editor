package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.EdgeC")
public abstract class EdgeC extends EdgeD
{
	private String ofC;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofC")
	public String getofC() {
	    return this.ofC;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofC")
	public void setofC(final String ofC) {
	    this.ofC = ofC;
	}
	
	private TC tc;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_tc")
	public TC gettc() {
	    return this.tc;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("tc")
	public void settc(final TC tc) {
	    this.tc = tc;
	}
	
	private java.util.List<TC> tcList;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_tcList")
	public java.util.List<TC> gettcList() {
	    return this.tcList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("tcList")
	public void settcList(final java.util.List<TC> tcList) {
	    this.tcList = tcList;
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
	
		
	public static EdgeC fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			return info.scce.pyro.hierarchy.rest.EdgeA.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static EdgeC fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			return info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

