package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

public class ReconnectEdgeCommand extends Command {
	
    @com.fasterxml.jackson.annotation.JsonProperty("oldSourceId")
    long oldSourceId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldSourceType")
    String oldSourceType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("sourceId")
    long sourceId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("sourceType")
    String sourceType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldTargetId")
    long oldTargetId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldTargetType")
    String oldTargetType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("targetId")
    long targetId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("targetType")
    String targetType;

    public long getOldSourceId() {
        return oldSourceId;
    }

    public void setOldSourceId(long oldSourceId) {
        this.oldSourceId = oldSourceId;
    }
    
    public String getOldSourceType() {
        return oldSourceType;
    }

    public void setOldSourceType(String oldSourceType) {
        this.oldSourceType = oldSourceType;
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

    public long getOldTargetId() {
        return oldTargetId;
    }

    public void setOldTargetId(long oldTargetId) {
        this.oldTargetId = oldTargetId;
    }
    
    public String getOldTargetType() {
        return oldTargetType;
    }

    public void setOldTargetType(String oldTargetType) {
        this.oldTargetType = oldTargetType;
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

    @Override
    protected void rewrite(long oldId, long newId) {
        if(sourceId==oldId) {
            sourceId = newId;
        }
        if(targetId==oldId) {
            targetId = newId;
        }
        if(oldSourceId==oldId) {
            oldSourceId = newId;
        }
        if(oldTargetId==oldId) {
            oldTargetId = newId;
        }
    }
}