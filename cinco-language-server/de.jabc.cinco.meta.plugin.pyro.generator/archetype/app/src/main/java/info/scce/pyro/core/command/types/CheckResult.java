package info.scce.pyro.core.command.types;


/**
 * Author zweihoff
 */

public class CheckResult {

    private String message;

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
    
    private String type;

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    public String getType() {
        return type;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }
}