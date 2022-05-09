package info.scce.pyro.api;

import entity.core.PyroUserDB;
import graphmodel.GraphModel;
import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.message.MessageDialog;
import info.scce.pyro.sync.GraphModelWebSocket;

/**
 * Author zweihoff
 */
public abstract class PyroControl {

    private MessageDialog messageDialog;

    private CommandExecuter cmdExecuter;
    
    @org.eclipse.xtext.xbase.lib.Extension
    protected de.jabc.cinco.meta.runtime.xapi.GraphModelExtension _graphModelExtension = new de.jabc.cinco.meta.runtime.xapi.GraphModelExtension();

    public final void init(CommandExecuter cmdExecuter) {
        this.cmdExecuter = cmdExecuter;
        GraphModelWebSocket graphModelWebSocket = cmdExecuter == null?
    		null:
    		cmdExecuter.getGraphModelWebSocket();
        messageDialog = new MessageDialog(cmdExecuter, graphModelWebSocket);
    }

    public final CommandExecuter commandExecuter() {
        return cmdExecuter;
    }
    
    public final PyroUserDB getCurrentUser() {
        return this.cmdExecuter.getBatch().getUser();
    }

    public final MessageDialog messageDialog() {
        return messageDialog;
    }

    
    protected final void openFile(GraphModel g) {
        cmdExecuter.openFile(g.getDelegate());
    }
    
    protected final void openFile(io.quarkus.hibernate.orm.panache.PanacheEntity file) {
        cmdExecuter.openFile(file);
    }
}

