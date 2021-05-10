package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.ContC")
public abstract class ContC extends ContD
{
	private String ofContC;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofContC")
	public String getofContC() {
	    return this.ofContC;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofContC")
	public void setofContC(final String ofContC) {
	    this.ofContC = ofContC;
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
	
	private long x;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_x")
	public long getx() {
		return this.x;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("x")
	public void setx(final long x) {
		this.x = x;
	}
	
	private long y;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_y")
	public long gety() {
		return this.y;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("y")
	public void sety(final long y) {
		this.y = y;
	}
	
	private long angle;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_angle")
	public long getangle() {
		return this.angle;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("angle")
	public void setangle(final long angle) {
		this.angle = angle;
	}
	
	private long width;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_width")
	public long getwidth() {
		return this.width;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("width")
	public void setwidth(final long width) {
		this.width = width;
	}
	
	private long height;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_height")
	public long getheight() {
		return this.height;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("height")
	public void setheight(final long height) {
		this.height = height;
	}
	
	private java.util.List<info.scce.pyro.core.graphmodel.Edge> incoming;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_incoming")
	public java.util.List<info.scce.pyro.core.graphmodel.Edge> getincoming() {
		return this.incoming;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("incoming")
	public void setincoming(final java.util.List<info.scce.pyro.core.graphmodel.Edge> incoming) {
		this.incoming = incoming;
	}
	
	private java.util.List<info.scce.pyro.core.graphmodel.Edge> outgoing;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_outgoing")
	public java.util.List<info.scce.pyro.core.graphmodel.Edge> getoutgoing() {
		return this.outgoing;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("outgoing")
	public void setoutgoing(final java.util.List<info.scce.pyro.core.graphmodel.Edge> outgoing) {
		this.outgoing = outgoing;
	}
	private java.util.List<info.scce.pyro.core.graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_modelElements")
	public java.util.List<info.scce.pyro.core.graphmodel.ModelElement> getmodelElements() {
	   return this.modelElements;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("modelElements")
	public void setmodelElements(final java.util.List<info.scce.pyro.core.graphmodel.ModelElement> modelElements) {
	   this.modelElements = modelElements;
	}
		
	public static ContC fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.ContADB) {
			return info.scce.pyro.hierarchy.rest.ContA.fromEntity(dbEntity, objectCache);
		} else if(dbEntity instanceof entity.hierarchy.ContDB) {
			return info.scce.pyro.hierarchy.rest.Cont.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static ContC fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.ContADB) {
			return info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties(dbEntity, objectCache);
		} else if(dbEntity instanceof entity.hierarchy.ContDB) {
			return info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

