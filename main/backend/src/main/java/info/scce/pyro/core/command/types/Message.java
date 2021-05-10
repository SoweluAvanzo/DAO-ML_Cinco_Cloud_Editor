package info.scce.pyro.core.command.types;


/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
@com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.GraphMessage", value = GraphMessage.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.ProjectMessage", value = ProjectMessage.class)
})
public abstract class Message {
	
    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
    private String messageType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("senderId")
    private long senderId;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
}