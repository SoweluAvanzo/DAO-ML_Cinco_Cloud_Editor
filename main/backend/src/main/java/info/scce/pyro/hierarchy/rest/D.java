package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.D")
public class D implements info.scce.pyro.core.graphmodel.Node
{
	private String ofD;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofD")
	public String getofD() {
	    return this.ofD;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofD")
	public void setofD(final String ofD) {
	    this.ofD = ofD;
	}
	
	private TD td;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_td")
	public TD gettd() {
	    return this.td;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("td")
	public void settd(final TD td) {
	    this.td = td;
	}
	
	private java.util.List<TD> tdList;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_tdList")
	public java.util.List<TD> gettdList() {
	    return this.tdList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("tdList")
	public void settdList(final java.util.List<TD> tdList) {
	    this.tdList = tdList;
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
		
	public static D fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.DDB) {
			entity.hierarchy.DDB entity = (entity.hierarchy.DDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final D result;
			result = new D();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			PanacheEntity dbContainer = entity.getContainer();
			info.scce.pyro.core.graphmodel.IdentifiableElement restContainer = TypeRegistry.getDBToRest(dbContainer, objectCache);
			result.setcontainer(restContainer);
			result.setwidth(entity.width);
			result.setheight(entity.height);
			result.setx(entity.x);
			result.sety(entity.y);
			java.util.List<info.scce.pyro.core.graphmodel.Edge> incomings = new java.util.LinkedList<>();
			java.util.Collection<PanacheEntity> dbIncoming = entity.getIncoming();
			incomings.addAll(dbIncoming.stream()
				.map((n)-> (info.scce.pyro.core.graphmodel.Edge) TypeRegistry.getDBToRest(n, objectCache))
				.collect(java.util.stream.Collectors.toList()));
			result.setincoming(incomings);
			java.util.List<info.scce.pyro.core.graphmodel.Edge> outgoings = new java.util.LinkedList<>();
			java.util.Collection<PanacheEntity> dbOutgoing = entity.getOutgoing();
			outgoings.addAll(dbOutgoing.stream()
				.map((n)-> (info.scce.pyro.core.graphmodel.Edge) TypeRegistry.getDBToRest(n, objectCache))
				.collect(java.util.stream.Collectors.toList()));
			result.setoutgoing(outgoings);
			//additional attributes
			result.setofD(entity.ofD);
			
			
			
			PanacheEntity dbTd = entity.getTd();
			TD restTd = (info.scce.pyro.hierarchy.rest.TD) TypeRegistry.getDBToRest(dbTd, objectCache, false);
			result.settd(restTd);	
			
			
			java.util.Collection<PanacheEntity> dbTdList = entity.getTdList();
			result.settdList(dbTdList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TD) TypeRegistry.getDBToRest(n, objectCache, false))
				.collect(java.util.stream.Collectors.toList()));
			return result;
		}
		// delegating to subTypes
		else if(dbEntity instanceof entity.hierarchy.ADB) {
			return info.scce.pyro.hierarchy.rest.A.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static D fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.DDB) {
			entity.hierarchy.DDB entity = (entity.hierarchy.DDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final D result;
			result = new D();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setofD(entity.ofD);
			
			
			
			PanacheEntity dbTd = entity.getTd();
			TD restTd = (info.scce.pyro.hierarchy.rest.TD) TypeRegistry.getDBToRest(dbTd, objectCache, true);
			result.settd(restTd);	
			
			
			java.util.Collection<PanacheEntity> dbTdList = entity.getTdList();
			result.settdList(dbTdList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TD) TypeRegistry.getDBToRest(n, objectCache, true))
				.collect(java.util.stream.Collectors.toList()));
			return result;
		}
		// delegating to subTypes
		else if(dbEntity instanceof entity.hierarchy.ADB) {
			return info.scce.pyro.hierarchy.rest.A.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

