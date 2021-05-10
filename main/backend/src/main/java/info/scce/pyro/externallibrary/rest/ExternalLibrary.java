package info.scce.pyro.externallibrary.rest;


/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.externallibrary.rest.ExternalLibrary")
public class ExternalLibrary implements info.scce.pyro.core.graphmodel.IdentifiableElement
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
	
	private String filename;
	
	@com.fasterxml.jackson.annotation.JsonProperty("filename")
	public String getfilename() {
		return this.filename;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("filename")
	public void setfilename(final String filename) {
		this.filename = filename;
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
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityLibrary> ExternalActivityLibrary;
				
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivityLibrary")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityLibrary> getExternalActivityLibrary() {
	    return this.ExternalActivityLibrary;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivityLibrary")
	public void setExternalActivityLibrary(final java.util.Collection<info.scce.pyro.externallibrary.rest.ExternalActivityLibrary> ExternalActivityLibrary) {
	    this.ExternalActivityLibrary = ExternalActivityLibrary.stream().collect(java.util.stream.Collectors.toList());
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> ExternalActivityA;
				
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivityA")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityA> getExternalActivityA() {
	    return this.ExternalActivityA;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivityA")
	public void setExternalActivityA(final java.util.Collection<info.scce.pyro.externallibrary.rest.ExternalActivityA> ExternalActivityA) {
	    this.ExternalActivityA = ExternalActivityA.stream().collect(java.util.stream.Collectors.toList());
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> ExternalAbstractActivityB;
				
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalAbstractActivityB")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> getExternalAbstractActivityB() {
	    return this.ExternalAbstractActivityB;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalAbstractActivityB")
	public void setExternalAbstractActivityB(final java.util.Collection<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB> ExternalAbstractActivityB) {
	    this.ExternalAbstractActivityB = ExternalAbstractActivityB.stream().collect(java.util.stream.Collectors.toList());
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC> ExternalAbstractActivityC;
				
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalAbstractActivityC")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC> getExternalAbstractActivityC() {
	    return this.ExternalAbstractActivityC;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalAbstractActivityC")
	public void setExternalAbstractActivityC(final java.util.Collection<info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC> ExternalAbstractActivityC) {
	    this.ExternalAbstractActivityC = ExternalAbstractActivityC.stream().collect(java.util.stream.Collectors.toList());
	}
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> ExternalActivityD;
				
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivityD")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivityD> getExternalActivityD() {
	    return this.ExternalActivityD;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivityD")
	public void setExternalActivityD(final java.util.Collection<info.scce.pyro.externallibrary.rest.ExternalActivityD> ExternalActivityD) {
	    this.ExternalActivityD = ExternalActivityD.stream().collect(java.util.stream.Collectors.toList());
	}

    public static ExternalLibrary fromEntity(final entity.externallibrary.ExternalLibraryDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache != null && objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final ExternalLibrary result;
		result = new ExternalLibrary();
		if(objectCache != null) {
			objectCache.putRestTo(entity, result);
		}
		result.setId(entity.id);
		result.set__type("externallibrary.ExternalLibrary");
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		result.setExternalActivityLibrary(
			entity.getExternalActivityLibrary().stream().map((n)->
				info.scce.pyro.externallibrary.rest.ExternalActivityLibrary.fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
		result.setExternalActivityA(
			entity.getExternalActivityA().stream().map((n)->
				info.scce.pyro.externallibrary.rest.ExternalActivityA.fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
		result.setExternalAbstractActivityB(
			entity.getExternalAbstractActivityB().stream().map((n)->
				info.scce.pyro.externallibrary.rest.ExternalAbstractActivityB.fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
		result.setExternalAbstractActivityC(
			entity.getExternalAbstractActivityC().stream().map((n)->
				info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC.fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
		result.setExternalActivityD(
			entity.getExternalActivityD().stream().map((n)->
				info.scce.pyro.externallibrary.rest.ExternalActivityD.fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
		return result;
    }
}
