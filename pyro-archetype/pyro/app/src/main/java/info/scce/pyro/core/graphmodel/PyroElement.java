package info.scce.pyro.core.graphmodel;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public interface PyroElement extends info.scce.pyro.rest.RESTBaseType {
	
	@com.fasterxml.jackson.annotation.JsonProperty(info.scce.pyro.util.Constants.PYRO_ID)
	public long getId();

	@com.fasterxml.jackson.annotation.JsonProperty(info.scce.pyro.util.Constants.PYRO_ID)
	public void setId(long id);
	
    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public String get__type();

    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public void set__type(final String __type);
}