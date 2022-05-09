package info.scce.pyro.core.graphmodel;

/**
 * Author zweihoff
 */

public class PyroEnum {
	
    private String literal;

    @com.fasterxml.jackson.annotation.JsonProperty("literal")
    public String getliteral() {
        return this.literal;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("literal")
    public void setliteral(final String literal) {
        this.literal = literal;
    }

    public static PyroEnum fromEntity(String literal) {
        final PyroEnum result = new PyroEnum();
        result.setliteral(literal);
        return result;
    }
}