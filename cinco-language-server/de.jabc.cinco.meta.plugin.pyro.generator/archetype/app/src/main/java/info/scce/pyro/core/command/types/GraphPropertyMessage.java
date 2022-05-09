package info.scce.pyro.core.command.types;

import info.scce.pyro.core.rest.types.GraphModelProperty;

/**
 * Author zweihoff
 */

public class GraphPropertyMessage extends Message {
    @com.fasterxml.jackson.annotation.JsonProperty("graph")
    private GraphModelProperty graph;

    public GraphPropertyMessage() {
        super();
        super.setMessageType("graph");
    }

    public GraphModelProperty getGraph() {
        return graph;
    }

    public void setGraph(GraphModelProperty graph) {
        this.graph = graph;
    }
}