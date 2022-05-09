package info.scce.pyro.core.command.types;

import java.util.List;

/**
 * Author zweihoff
 */

public class CompoundCommandMessage extends GraphMessage {

    @com.fasterxml.jackson.annotation.JsonProperty("cmd")
    private CompoundCommand cmd;

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private String type;

    @com.fasterxml.jackson.annotation.JsonProperty("openFile")
    OpenFileCommand openFile;

    @com.fasterxml.jackson.annotation.JsonProperty("highlightings")
    List<HighlightCommand> highlightings;

    @com.fasterxml.jackson.annotation.JsonProperty("rewriteRule")
    List<RewriteRule> rewriteRule = new java.util.LinkedList<>(); 

    public CompoundCommandMessage() {
        super();
        super.setMessageType("command");
    }

    public CompoundCommand getCmd() {
        return cmd;
    }

    public void setCmd(CompoundCommand cmd) {
        this.cmd = cmd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HighlightCommand> getHighlightings() {
        return highlightings;
    }

    public void setHighlightings(List<HighlightCommand> highlightings) {
        this.highlightings = highlightings;
    }

    public List<RewriteRule> getRewriteRule() {
        return rewriteRule;
    }

    public void setRewriteRule(List<RewriteRule> rewriteRule) {
        this.rewriteRule = rewriteRule;
    }

    public OpenFileCommand getOpenFile() {
        return openFile;
    }

    public void setOpenFile(OpenFileCommand openFile) {
        this.openFile = openFile;
    }

    public void rewriteId(long oldId, long newId) {
        getCmd().rewriteId(oldId,newId);
        rewriteRule.add(new RewriteRule(oldId,newId));
        getHighlightings().stream().filter(n->n.getId()==oldId).forEach(n->n.setId(newId));
    }
}