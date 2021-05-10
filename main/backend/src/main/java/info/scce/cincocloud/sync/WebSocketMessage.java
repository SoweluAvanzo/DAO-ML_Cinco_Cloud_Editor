package info.scce.cincocloud.sync;

@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
public class WebSocketMessage {

    private long senderId;
    private String event;
    private Object content;

    public static WebSocketMessage fromEntity(final long userId, final Object content) {
        return WebSocketMessage.fromEntity(userId, "", content);
    }

    public static WebSocketMessage fromEntity(final long userId, final String event, final Object content) {
        final WebSocketMessage result = new WebSocketMessage();
        result.setsenderId(userId);
        result.setevent(event);
        result.setcontent(content);
        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("senderId")
    public long getsenderId() {
        return this.senderId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("senderId")
    public void setsenderId(final long senderId) {
        this.senderId = senderId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("event")
    public String getevent() {
        return this.event;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("event")
    public void setevent(final String event) {
        this.event = event;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public Object getcontent() {
        return this.content;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public void setcontent(final Object content) {
        this.content = content;
    }
}
