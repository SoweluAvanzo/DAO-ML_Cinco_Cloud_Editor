package info.scce.pyro.core.graphmodel;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public interface Edge extends ModelElement {
	
    @com.fasterxml.jackson.annotation.JsonProperty("a_sourceElement")
    public Node getsourceElement();

    @com.fasterxml.jackson.annotation.JsonProperty("sourceElement")
    public void setsourceElement(final Node sourceElement);
    
    @com.fasterxml.jackson.annotation.JsonProperty("a_targetElement")
    public Node gettargetElement();

    @com.fasterxml.jackson.annotation.JsonProperty("targetElement")
    public void settargetElement(final Node targetElement);
    
    @com.fasterxml.jackson.annotation.JsonProperty("a_bendingPoints")
    public java.util.List<BendingPoint> getbendingPoints();

    @com.fasterxml.jackson.annotation.JsonProperty("bendingPoints")
    public void setbendingPoints(final java.util.List<BendingPoint> bendingPoints);
}