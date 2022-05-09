package info.scce.pyro.core.command.types;

import java.util.List;

/**
 * Author zweihoff
 */

public class Action {
	
    @com.fasterxml.jackson.annotation.JsonProperty("fqn")
    private String fqn;

    @com.fasterxml.jackson.annotation.JsonProperty("highlightings")
    private List<HighlightCommand> highlightings;

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public List<HighlightCommand> getHighlightings() {
        return highlightings;
    }

    public void setHighlightings(List<HighlightCommand> highlightings) {
        this.highlightings = highlightings;
    }

}