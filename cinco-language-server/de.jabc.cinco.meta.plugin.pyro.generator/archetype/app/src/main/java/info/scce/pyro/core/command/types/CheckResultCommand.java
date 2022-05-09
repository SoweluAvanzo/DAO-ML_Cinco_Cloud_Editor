package info.scce.pyro.core.command.types;


/**
 * Author zweihoff
 */

public class CheckResultCommand extends Command {

    private java.util.List<CheckResult> results = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("results")
    public java.util.List<CheckResult> getResults() {
        return results;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    public void setMessage(java.util.List<CheckResult> results) {
        this.results = results;
    }
    
    public static CheckResultCommand fromElement(graphmodel.IdentifiableElement element) {
        CheckResultCommand crc = new CheckResultCommand();
        crc.setDelegateId(element.getDelegateId());
        return crc;
    }
    
    public void addResult(String msg, String type) {
    	CheckResult cr = new CheckResult();
    	cr.setMessage(msg);
    	cr.setType(type);
    	results.add(cr);
    }

    @Override
    protected void rewrite(long oldId, long newId) {
        
    }
}