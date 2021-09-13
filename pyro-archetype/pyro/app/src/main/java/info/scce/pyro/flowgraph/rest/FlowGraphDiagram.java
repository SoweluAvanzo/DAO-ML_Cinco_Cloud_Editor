package info.scce.pyro.flowgraph.rest;

/**
 * Author zweihoff
 */

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.flowgraph.rest.FlowGraphDiagram")
public class FlowGraphDiagram implements info.scce.pyro.core.graphmodel.GraphModel, info.scce.pyro.core.rest.types.IPyroFile
{
	private String modelName;
	
	@com.fasterxml.jackson.annotation.JsonProperty("c_modelName")
	public String getmodelName() {
	    return this.modelName;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("modelName")
	public void setmodelName(final String modelName) {
	    this.modelName = modelName;
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
	private java.util.List<info.scce.pyro.core.graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_modelElements")
	public java.util.List<info.scce.pyro.core.graphmodel.ModelElement> getmodelElements() {
	   return this.modelElements;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("modelElements")
	public void setmodelElements(final java.util.List<info.scce.pyro.core.graphmodel.ModelElement> modelElements) {
	   this.modelElements = modelElements;
	}
	private Double scale;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_scale")
	public Double getscale() {
	    return this.scale;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("scale")
	public void setscale(final Double scale) {
	    this.scale = scale;
	}
	
	private Long width;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_width")
	public Long getwidth() {
	    return this.width;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("width")
	public void setwidth(final Long width) {
	    this.width = width;
	}
	
	private Long height;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_height")
	public Long getheight() {
	    return this.height;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("height")
	public void setheight(final Long height) {
	    this.height = height;
	}
	
	private String filename;
	
	@com.fasterxml.jackson.annotation.JsonProperty("filename")
	public String getfilename() {
	    return this.filename;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("filename")
	public void setfilename(final String filename) {
	    this.filename = filename;
	}
	
	private boolean isPublic;
					
	@com.fasterxml.jackson.annotation.JsonProperty("isPublic")
	public boolean getisPublic() {
	    return this.isPublic;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("isPublic")
	public void setisPublic(final boolean isPublic) {
	    this.isPublic = isPublic;
	}
	
	private String extension;
	
	@com.fasterxml.jackson.annotation.JsonProperty("extension")
	public String getextension() {
	    return this.extension;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("extension")
	public void setextension(final String extension) {
	    this.extension = extension;
	}
	
	
	private String router;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_router")
	public String getrouter() {
	    return this.router;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("router")
	public void setrouter(final String router) {
	    this.router = router;
	}
	
	private String connector;
	
	@com.fasterxml.jackson.annotation.JsonProperty("a_connector")
	public String getconnector() {
	    return this.connector;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("connector")
	public void setconnector(final String connector) {
	    this.connector = connector;
	}
		
	public static FlowGraphDiagram fromEntity(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.flowgraph.FlowGraphDiagramDB) {
			entity.flowgraph.FlowGraphDiagramDB entity = (entity.flowgraph.FlowGraphDiagramDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final FlowGraphDiagram result;
			result = new FlowGraphDiagram();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			java.util.Collection<PanacheEntity> dbModelElements = entity.getModelElements();
			result.getmodelElements().addAll(
				dbModelElements.stream()
					.map( (n) -> (info.scce.pyro.core.graphmodel.ModelElement) TypeRegistry.getDBToRest(n, objectCache))
					.collect(java.util.stream.Collectors.toList())
			);
			result.setscale(entity.scale);
			result.setwidth(entity.width);
			result.setheight(entity.height);
			result.setrouter(entity.router);
			result.setconnector(entity.connector);
			result.setfilename(entity.filename);
			result.setextension(entity.extension);
			result.setisPublic(entity.isPublic);
			//additional attributes
			result.setmodelName(entity.modelName);
			return result;
		}
		else
			return null;
	}
	
	public static FlowGraphDiagram fromEntityProperties(final PanacheEntity dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(dbEntity instanceof entity.flowgraph.FlowGraphDiagramDB) {
			entity.flowgraph.FlowGraphDiagramDB entity = (entity.flowgraph.FlowGraphDiagramDB) dbEntity;
			if(objectCache!=null&&objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final FlowGraphDiagram result;
			result = new FlowGraphDiagram();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type(TypeRegistry.getTypeOf(entity));
			//additional attributes
			result.setmodelName(entity.modelName);
			return result;
		}
		else
			return null;
	}
}
