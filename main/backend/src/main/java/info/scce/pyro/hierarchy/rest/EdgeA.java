package info.scce.pyro.hierarchy.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.hierarchy.rest.EdgeA")
public class EdgeA extends EdgeB
{
	private String ofA;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ofA")
	public String getofA() {
	    return this.ofA;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ofA")
	public void setofA(final String ofA) {
	    this.ofA = ofA;
	}
	
	private TA ta;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_ta")
	public TA getta() {
	    return this.ta;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ta")
	public void setta(final TA ta) {
	    this.ta = ta;
	}
	
	private java.util.List<TA> taList;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_taList")
	public java.util.List<TA> gettaList() {
	    return this.taList;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("taList")
	public void settaList(final java.util.List<TA> taList) {
	    this.taList = taList;
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
	
		
	public static EdgeA fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			entity.hierarchy.EdgeADB entity = (entity.hierarchy.EdgeADB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final EdgeA result;
			result = new EdgeA();
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
			result.setofA(entity.ofA);
			
			
			
			PanacheEntity dbTa = entity.getTa();
			TA restTa = (info.scce.pyro.hierarchy.rest.TA) TypeRegistry.getDBToRest(dbTa, objectCache, false);
			result.setta(restTa);	
			
			
			java.util.Collection<PanacheEntity> dbTaList = entity.getTaList();
			result.settaList(dbTaList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TA) TypeRegistry.getDBToRest(n, objectCache, false))
				.collect(java.util.stream.Collectors.toList()));
			
			result.setofB(entity.ofB);
			
			
			
			PanacheEntity dbTb = entity.getTb();
			TB restTb = (info.scce.pyro.hierarchy.rest.TB) TypeRegistry.getDBToRest(dbTb, objectCache, false);
			result.settb(restTb);	
			
			
			java.util.Collection<PanacheEntity> dbTbList = entity.getTbList();
			result.settbList(dbTbList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TB) TypeRegistry.getDBToRest(n, objectCache, false))
				.collect(java.util.stream.Collectors.toList()));
			
			result.setofC(entity.ofC);
			
			
			
			PanacheEntity dbTc = entity.getTc();
			TC restTc = (info.scce.pyro.hierarchy.rest.TC) TypeRegistry.getDBToRest(dbTc, objectCache, false);
			result.settc(restTc);	
			
			
			java.util.Collection<PanacheEntity> dbTcList = entity.getTcList();
			result.settcList(dbTcList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TC) TypeRegistry.getDBToRest(n, objectCache, false))
				.collect(java.util.stream.Collectors.toList()));
			
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
		else
			return null;
	}
	
	public static EdgeA fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.hierarchy.EdgeADB) {
			entity.hierarchy.EdgeADB entity = (entity.hierarchy.EdgeADB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final EdgeA result;
			result = new EdgeA();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setofA(entity.ofA);
			
			
			
			PanacheEntity dbTa = entity.getTa();
			TA restTa = (info.scce.pyro.hierarchy.rest.TA) TypeRegistry.getDBToRest(dbTa, objectCache, true);
			result.setta(restTa);	
			
			
			java.util.Collection<PanacheEntity> dbTaList = entity.getTaList();
			result.settaList(dbTaList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TA) TypeRegistry.getDBToRest(n, objectCache, true))
				.collect(java.util.stream.Collectors.toList()));
			
			result.setofB(entity.ofB);
			
			
			
			PanacheEntity dbTb = entity.getTb();
			TB restTb = (info.scce.pyro.hierarchy.rest.TB) TypeRegistry.getDBToRest(dbTb, objectCache, true);
			result.settb(restTb);	
			
			
			java.util.Collection<PanacheEntity> dbTbList = entity.getTbList();
			result.settbList(dbTbList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TB) TypeRegistry.getDBToRest(n, objectCache, true))
				.collect(java.util.stream.Collectors.toList()));
			
			result.setofC(entity.ofC);
			
			
			
			PanacheEntity dbTc = entity.getTc();
			TC restTc = (info.scce.pyro.hierarchy.rest.TC) TypeRegistry.getDBToRest(dbTc, objectCache, true);
			result.settc(restTc);	
			
			
			java.util.Collection<PanacheEntity> dbTcList = entity.getTcList();
			result.settcList(dbTcList.stream()
				.map((n)-> (info.scce.pyro.hierarchy.rest.TC) TypeRegistry.getDBToRest(n, objectCache, true))
				.collect(java.util.stream.Collectors.toList()));
			
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
		else
			return null;
	}
}

