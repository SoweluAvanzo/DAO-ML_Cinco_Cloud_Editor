package info.scce.cincocloud.sync;

import java.util.LinkedList;
import java.util.List;

/**
 * Author zweihoff
 */
public class DisplayMessages {
    private List<DisplayMessage> messages = new LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("messages")
    public List<DisplayMessage> getMessages() {
        return this.messages;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("messages")
    public void setMessages(final List<DisplayMessage> messages) {
        this.messages = messages;
    }

}