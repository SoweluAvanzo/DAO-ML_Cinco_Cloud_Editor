package info.scce.cincocloud.sync;

/**
 * Author zweihoff
 */
public class DisplayMessage {
    private String messageType;
    private String content;

    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
    public String getMessageType() {
        return this.messageType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public String getContent() {
        return this.content;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public void setContent(final String content) {
        this.content = content;
    }


}