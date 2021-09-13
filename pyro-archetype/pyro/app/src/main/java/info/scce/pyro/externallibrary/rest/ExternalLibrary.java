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
	
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivity> ExternalActivity;
				
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivity")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalActivity> getExternalActivity() {
	    return this.ExternalActivity;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("ExternalActivity")
	public void setExternalActivity(final java.util.Collection<info.scce.pyro.externallibrary.rest.ExternalActivity> ExternalActivity) {
	    this.ExternalActivity = ExternalActivity.stream().collect(java.util.stream.Collectors.toList());
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
		result.setExternalActivity(
			entity.getExternalActivity().stream().map((n)->
				info.scce.pyro.externallibrary.rest.ExternalActivity.fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
		return result;
    }
}
