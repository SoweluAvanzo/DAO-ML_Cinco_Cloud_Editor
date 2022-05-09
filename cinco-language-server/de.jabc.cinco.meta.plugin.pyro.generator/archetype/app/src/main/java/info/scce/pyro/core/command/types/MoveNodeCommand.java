package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

public class MoveNodeCommand extends Command {
	
    @com.fasterxml.jackson.annotation.JsonProperty("oldX")
    long oldX;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldY")
    long oldY;
    
    @com.fasterxml.jackson.annotation.JsonProperty("x")
    long x;
    
    @com.fasterxml.jackson.annotation.JsonProperty("y")
    long y;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldContainerId")
    long oldContainerId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldContainerType")
    String oldContainerType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("containerId")
    long containerId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("containerType")
    String containerType;

    public long getOldX() {
        return oldX;
    }

    public void setOldX(long oldX) {
        this.oldX = oldX;
    }

    public long getOldY() {
        return oldY;
    }

    public void setOldY(long oldY) {
        this.oldY = oldY;
    }

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

    public long getOldContainerId() {
        return oldContainerId;
    }

    public void setOldContainerId(long oldContainerId) {
        this.oldContainerId = oldContainerId;
    }
    

    public void setOldContainerType(String oldContainerType) {
        this.oldContainerType = oldContainerType;
    }

    public String getOldContainerType() {
        return oldContainerType;
    }
    
    public long getContainerId() {
        return containerId; // NOTE: changed from oldContainerId to containerId
    }

    public void setContainerId(long containerId) {
        this.containerId = containerId;
    }
    
    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public String getContainerType() {
        return containerType;
    }

    @Override
    protected void rewrite(long oldId, long newId) {
        if(oldContainerId==oldId){
            oldContainerId = newId;
        }
        if(containerId==oldId){
            containerId = newId;
        }
    }
}