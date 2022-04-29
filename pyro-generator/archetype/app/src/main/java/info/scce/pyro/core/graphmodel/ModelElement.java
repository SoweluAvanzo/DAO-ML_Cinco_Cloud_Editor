package info.scce.pyro.core.graphmodel;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public interface ModelElement extends IdentifiableElement {
	
    @com.fasterxml.jackson.annotation.JsonProperty("a_container")
    public IdentifiableElement getcontainer();

    @com.fasterxml.jackson.annotation.JsonProperty("container")
    public void setcontainer(final IdentifiableElement container);
}