package info.scce.pyro.core.graphmodel;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public interface Container extends Node {
	
    @com.fasterxml.jackson.annotation.JsonProperty("a_modelElements")
    public java.util.List<ModelElement> getmodelElements();

    @com.fasterxml.jackson.annotation.JsonProperty("modelElements")
    public void setmodelElements(final java.util.List<ModelElement> modelElements);
}