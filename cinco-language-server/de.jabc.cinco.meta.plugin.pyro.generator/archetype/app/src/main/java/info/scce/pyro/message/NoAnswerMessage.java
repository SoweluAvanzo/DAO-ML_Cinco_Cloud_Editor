package info.scce.pyro.message;


/**
 * Author zweihoff
 */
@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public class NoAnswerMessage extends DialogMessage{

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private String type;

    public NoAnswerMessage() {
        messageType = "message_dialog_no_answer";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
