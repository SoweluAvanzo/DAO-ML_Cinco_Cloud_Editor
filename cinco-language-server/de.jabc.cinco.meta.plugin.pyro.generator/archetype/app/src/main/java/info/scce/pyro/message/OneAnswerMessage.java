package info.scce.pyro.message;

import java.util.List;

/**
 * Author zweihoff
 */
@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public class OneAnswerMessage extends DialogMessage {

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private long id;

    @com.fasterxml.jackson.annotation.JsonProperty("choices")
    private List<String> choices;

    public OneAnswerMessage() {
        messageType = "message_dialog_one_answer";
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
