package info.scce.pyro.plugin.rest;


import java.util.List;

/**
 * Author zweihoff
 */

public class TreeViewRest {

    private List<TreeViewNodeRest> layer;

    @com.fasterxml.jackson.annotation.JsonProperty("layer")
    public List<TreeViewNodeRest> getlayer() {
        return this.layer;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("layer")
    public void setlayer(final List<TreeViewNodeRest> layer) {
        this.layer = layer;
    }
}
