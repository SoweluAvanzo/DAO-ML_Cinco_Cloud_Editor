package info.scce.pyro.core.command.types;

import java.util.LinkedList;
import java.util.List;

/**
 * Author zweihoff
 */

public class CompoundCommand {

    @com.fasterxml.jackson.annotation.JsonProperty("queue")
    java.util.List<Command> queue = new LinkedList<>();

    public List<Command> getQueue() {
        return queue;
    }

    public void setQueue(List<Command> queue) {
        this.queue = queue;
    }

    public void rewriteId(long oldId,long newId) {
        queue.forEach(n->n.rewriteId(oldId,newId));
    }
}