package info.scce.pyro.core.command.types;

import info.scce.pyro.core.graphmodel.BendingPoint;
import info.scce.pyro.core.graphmodel.IdentifiableElement;

import java.util.LinkedList;
import java.util.List;

/**
 * Author zweihoff
 */

public class RemoveEdgeCommand extends Command {
	
    @com.fasterxml.jackson.annotation.JsonProperty("sourceId")
    long sourceId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("sourceType")
    String sourceType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("targetId")
    long targetId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("targetType")
    String targetType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("positions")
    java.util.List<BendingPoint> positions;
    
    @com.fasterxml.jackson.annotation.JsonProperty("element")
    IdentifiableElement element;

    public RemoveEdgeCommand(){
        positions = new LinkedList<>();
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }
    
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public List<BendingPoint> getPositions() {
        return positions;
    }

    public void setPositions(List<BendingPoint> positions) {
        this.positions = positions;
    }
    
    public IdentifiableElement getElement() {
        return element;
    }

    public void setElement(IdentifiableElement element) {
        this.element = element;
    }

    @Override
    protected void rewrite(long oldId, long newId) {
        if(sourceId == oldId) {
            sourceId = newId;
        }
        if(targetId == oldId) {
            targetId = newId;
        }
        if(element!=null&&element.getId()==oldId) {
        	element.setId(newId);
        }
    }
}