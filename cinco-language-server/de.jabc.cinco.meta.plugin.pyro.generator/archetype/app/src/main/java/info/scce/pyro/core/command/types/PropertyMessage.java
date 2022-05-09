package info.scce.pyro.core.command.types;

import info.scce.pyro.core.graphmodel.IdentifiableElement;

/**
 * Author zweihoff
 */

public class PropertyMessage extends GraphMessage {

    @com.fasterxml.jackson.annotation.JsonProperty("graphModelType")
    private String graphModelType;

    @com.fasterxml.jackson.annotation.JsonProperty("delegate")
    private IdentifiableElement delegate;

    @com.fasterxml.jackson.annotation.JsonProperty("prevDelegate")
    private IdentifiableElement prevDelegate;

    public PropertyMessage() {
        super();
        super.setMessageType("property");
    }

    public String getGraphModelType() {
        return graphModelType;
    }

    public void setGraphModelType(String graphModelType) {
        this.graphModelType = graphModelType;
    }

    public IdentifiableElement getDelegate() {
        return delegate;
    }

    public void setDelegate(IdentifiableElement delegate) {
        this.delegate = delegate;
    }

    public IdentifiableElement getPrevDelegate() {
        return prevDelegate;
    }

    public void setPrevDelegate(IdentifiableElement delegate) {
        this.prevDelegate = delegate;
    }
}