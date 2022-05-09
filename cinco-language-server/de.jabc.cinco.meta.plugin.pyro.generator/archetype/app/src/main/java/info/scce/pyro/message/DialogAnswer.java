package info.scce.pyro.message;

/**
 * Author zweihoff
 */
@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public class DialogAnswer {

    @com.fasterxml.jackson.annotation.JsonProperty("senderId")
    protected long userId;

    @com.fasterxml.jackson.annotation.JsonProperty("dialogId")
    protected long dialogId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("answer")
    private String answer;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
