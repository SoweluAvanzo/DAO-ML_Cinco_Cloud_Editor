package info.scce.pyro.core.command.types;

import info.scce.pyro.core.graphmodel.IdentifiableElement;
import info.scce.pyro.core.graphmodel.PyroElement;

/**
 * Author zweihoff
 */

public class RemoveNodeCommand extends Command {
	
    @com.fasterxml.jackson.annotation.JsonProperty("x")
    long x;
    
    @com.fasterxml.jackson.annotation.JsonProperty("y")
    long y;
    
    @com.fasterxml.jackson.annotation.JsonProperty("width")
    long width;
    
    @com.fasterxml.jackson.annotation.JsonProperty("height")
    long height;
    
    @com.fasterxml.jackson.annotation.JsonProperty("containerId")
    long containerId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("containerType")
    String containerType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("primeId")
    long primeId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("primeElement")
    info.scce.pyro.core.graphmodel.PyroElement primeElement;
    
    @com.fasterxml.jackson.annotation.JsonProperty("element")
    IdentifiableElement element;

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getContainerId() {
        return containerId;
    }

    public void setContainerId(long containerId) {
        this.containerId = containerId;
    }

    public String getContainerTyoe() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public long getPrimeId() {
        return primeId;
    }

    public void setPrimeId(long primeId) {
        this.primeId = primeId;
    }

    public PyroElement getPrimeElement() {
        return primeElement;
    }

    public void setPrimeElement(PyroElement primeElement) {
        this.primeElement = primeElement;
    }
    
    public IdentifiableElement getElement() {
        return element;
    }

    public void setElement(IdentifiableElement element) {
        this.element = element;
    }

    @Override
    protected void rewrite(long oldId, long newId) {
        if(primeId == oldId) {
            primeId = newId;
        }
        if(containerId == oldId) {
            containerId = newId;
        }
        if(primeElement!=null&&primeElement.getId() == oldId) {
            primeElement.setId(newId);
        }
        if(element!=null&&element.getId()==oldId) {
        	element.setId(newId);
        }
    }
}