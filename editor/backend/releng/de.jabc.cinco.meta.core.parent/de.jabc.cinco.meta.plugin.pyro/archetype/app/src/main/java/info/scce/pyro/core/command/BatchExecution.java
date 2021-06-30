package info.scce.pyro.core.command;

import graphmodel.GraphModel;
import entity.core.PyroUserDB;
import info.scce.pyro.core.command.types.Command;

import java.util.LinkedList;
import java.util.List;

/**
 * Author zweihoff
 */
public class BatchExecution {
    final PyroUserDB user;
    final List<Command> commands;
    TransactionMode mode;
    final GraphModel graphModel;

    BatchExecution(PyroUserDB user,GraphModel graphModel){
        this.user = user;
        commands = new LinkedList<>();
        this.graphModel = graphModel;
    }

    void add(Command cmd){
        commands.add(cmd);
    }

    public PyroUserDB getUser() {
        return user;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public TransactionMode getMode() {
        return mode;
    }

    public void setMode(TransactionMode mode) {
        this.mode = mode;
    }
    
    public GraphModel getGraphModel(){
        return graphModel;
    }
}

enum TransactionMode {
    PROPAGATE, SILENT
}
