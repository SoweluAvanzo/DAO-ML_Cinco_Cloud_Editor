package info.scce.pyro.message;

/**
 * Author zweihoff
 */
@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public class DialogMessage {

    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
    protected String messageType = "message_dialog";
    
    @com.fasterxml.jackson.annotation.JsonProperty("title")
    private String title;

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
