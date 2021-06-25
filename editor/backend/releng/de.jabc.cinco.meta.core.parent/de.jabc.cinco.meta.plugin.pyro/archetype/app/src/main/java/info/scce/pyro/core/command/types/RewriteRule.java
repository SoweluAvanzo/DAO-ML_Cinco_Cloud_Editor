package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

public class RewriteRule {
	
    @com.fasterxml.jackson.annotation.JsonProperty("newId")
    long newId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("oldId")
    long oldId;
    
    public RewriteRule(long oldId, long newId) {
    	this.oldId = oldId;
    	this.newId = newId;
    }

    public long getNewId() {
    	return newId;
    }
    
    public long getOldId() {
    	return oldId;
    }
}