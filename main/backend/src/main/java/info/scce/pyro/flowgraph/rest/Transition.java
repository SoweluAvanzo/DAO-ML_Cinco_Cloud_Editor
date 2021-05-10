package info.scce.pyro.flowgraph.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.flowgraph.rest.Transition")
public class Transition implements info.scce.pyro.core.graphmodel.Edge
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
	
		
	public static Transition fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB entity = (entity.flowgraph.TransitionDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final Transition result;
			result = new Transition();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			PanacheEntity dbContainer = entity.getContainer();
			info.scce.pyro.core.graphmodel.IdentifiableElement restContainer = TypeRegistry.getDBToRest(dbContainer, objectCache);
			result.setcontainer(restContainer);
			result.setbendingPoints(entity.bendingPoints.stream().map(n->info.scce.pyro.core.graphmodel.BendingPoint.fromEntity(n)).collect(java.util.stream.Collectors.toList()));
			PanacheEntity dbTarget = entity.getTarget();
			info.scce.pyro.core.graphmodel.Node restTarget = (info.scce.pyro.core.graphmodel.Node) TypeRegistry.getDBToRest(dbTarget, objectCache);
			result.settargetElement(restTarget);
			PanacheEntity dbSource = entity.getSource();
			info.scce.pyro.core.graphmodel.Node restSource = (info.scce.pyro.core.graphmodel.Node) TypeRegistry.getDBToRest(dbSource, objectCache);
			result.setsourceElement(restSource);
			
			//additional attributes
			return result;
		}
		else
			return null;
	}
	
	public static Transition fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB entity = (entity.flowgraph.TransitionDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final Transition result;
			result = new Transition();
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

