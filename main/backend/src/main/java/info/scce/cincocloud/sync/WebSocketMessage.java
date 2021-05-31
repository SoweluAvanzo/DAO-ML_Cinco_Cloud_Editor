package info.scce.cincocloud.sync;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("PYRO_Selective_Filter")
public class WebSocketMessage {

    private long senderId;

    private String event;

    private Object content;

    public static WebSocketMessage fromEntity(final long senderId, final Object content) {
        return WebSocketMessage.fromEntity(senderId, "", content);
    }

    public static WebSocketMessage fromEntity(final long senderId, final String event, final Object content) {
        final var result = new WebSocketMessage();
        result.senderId = senderId;
        result.event = event;
        result.content = content;
        return result;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
