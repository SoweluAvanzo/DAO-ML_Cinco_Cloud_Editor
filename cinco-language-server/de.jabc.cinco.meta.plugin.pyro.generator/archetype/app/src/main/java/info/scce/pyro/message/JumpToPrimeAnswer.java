package info.scce.pyro.message;

/**
 * Author zweihoff
 */
@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public class JumpToPrimeAnswer {

    @com.fasterxml.jackson.annotation.JsonProperty("graphmodel_id")
    protected String graphModelId;

    @com.fasterxml.jackson.annotation.JsonProperty("element_id")
    protected String elementId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("graphmodel_type")
    private String graphModelType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("element_type")
    private String elementType;

    public String getGraphModelId() {
        return graphModelId;
    }

    public void setGraphModelId(String id) {
        this.graphModelId = id;
    }
    
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String id) {
        this.elementId = id;
    }
    
    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }
    
    public String getGraphModelType() {
        return graphModelType;
    }

    public void setGraphModelType(String graphModelType) {
        this.graphModelType = graphModelType;
    }

   
}
