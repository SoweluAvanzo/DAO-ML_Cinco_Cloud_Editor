package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
@com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name= "info.scce.pyro.core.command.types.CompoundCommandMessage", value = CompoundCommandMessage.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name= "info.scce.pyro.core.command.types.PropertyMessage", value = PropertyMessage.class)
})
public abstract class GraphMessage extends Message {
	
    @com.fasterxml.jackson.annotation.JsonProperty("graphModelId")
    long graphModelId;

    public long getGraphModelId() {
        return graphModelId;
    }

    public void setGraphModelId(long graphModelId) {
        this.graphModelId = graphModelId;
    }
}