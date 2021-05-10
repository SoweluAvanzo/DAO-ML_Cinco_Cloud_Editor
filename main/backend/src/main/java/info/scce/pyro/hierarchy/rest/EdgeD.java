package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.EdgeD")
public class EdgeD implements info.scce.pyro.core.graphmodel.Edge
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
	
		
	public static EdgeD fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeDDB) {
			entity.hierarchy.EdgeDDB entity = (entity.hierarchy.EdgeDDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final EdgeD result;
			result = new EdgeD();
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
		else if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			return info.scce.pyro.hierarchy.rest.EdgeA.fromEntity(dbEntity, objectCache);
		}
		else
			return null;
	}
	
	public static EdgeD fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeDDB) {
			entity.hierarchy.EdgeDDB entity = (entity.hierarchy.EdgeDDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final EdgeD result;
			result = new EdgeD();
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
		else if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			return info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(dbEntity, objectCache);
		}
		else
			return null;
	}
}

