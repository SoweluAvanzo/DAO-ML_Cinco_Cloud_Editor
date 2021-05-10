package info.scce.pyro.flowgraph.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.flowgraph.rest.EActivityA")
public class EActivityA implements info.scce.pyro.core.graphmodel.Node
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
	protected info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC activityC;
	
	@com.fasterxml.jackson.annotation.JsonProperty("b_activityC")
	public info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC getactivityC() {
	    return this.activityC;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("activityC")
	public void setactivityC(final info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC activityC) {
	    this.activityC = activityC;
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
		
	public static EActivityA fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.flowgraph.EActivityADB) {
			entity.flowgraph.EActivityADB entity = (entity.flowgraph.EActivityADB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final EActivityA result;
			result = new EActivityA();
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
			result.setactivityC(
				info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC.fromEntity(
					entity.getActivityC(), objectCache
				)
			);
			
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
			return result;
		}
		else
			return null;
	}
	
	public static EActivityA fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.flowgraph.EActivityADB) {
			entity.flowgraph.EActivityADB entity = (entity.flowgraph.EActivityADB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final EActivityA result;
			result = new EActivityA();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			return result;
		}
		else
			return null;
	}
}

