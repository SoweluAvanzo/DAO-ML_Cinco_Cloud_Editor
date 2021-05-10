package info.scce.pyro.api;

import entity.core.PyroFolderDB;
import entity.core.PyroProjectDB;
import entity.core.PyroUserDB;
import graphmodel.GraphModel;
import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.message.MessageDialog;
import de.jabc.cinco.meta.runtime.xapi.GraphModelExtension;

/**
 * Author zweihoff
 */
public abstract class PyroControl {

    private MessageDialog messageDialog;

    private CommandExecuter cmdExecuter;
    
    protected GraphModelExtension _graphModelExtension = new GraphModelExtension();


    public final void init(CommandExecuter cmdExecuter) {
        this.cmdExecuter = cmdExecuter;
        messageDialog = new MessageDialog(cmdExecuter, cmdExecuter.getGraphModelWebSocket());
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

    public final PyroProjectDB currentProject() {
        return cmdExecuter.getProject();
    }
    
    protected final void openFile(GraphModel g) {
        cmdExecuter.openFile(g.getDelegate());
    }
    
    protected final void openFile(io.quarkus.hibernate.orm.panache.PanacheEntity file) {
        cmdExecuter.openFile(file);
    }

    protected final io.quarkus.hibernate.orm.panache.PanacheEntity getParentFolder(graphmodel.GraphModel g) {
    	PyroProjectDB current = currentProject();
        return getParentFolder(current,g);
    }

    protected final PyroProjectDB getProject() {
        return this.commandExecuter().getProject();
    }
    
    private io.quarkus.hibernate.orm.panache.PanacheEntity getParentFolder(PyroProjectDB current, graphmodel.GraphModel g) {
    	if(current.getFiles().stream().filter(n->n.id == g.getDelegateId()).findFirst().isPresent()) {
            return current;
        }
    	for(PyroFolderDB pf:current.innerFolders) {
            PyroFolderDB result = getParentFolder(pf,g);
            if(result != null) {
                return result;
            }
        }
        return null;
    }
    
    private PyroFolderDB getParentFolder(PyroFolderDB current, graphmodel.GraphModel g) {
        if(current.getFiles().stream().filter(n->n.id ==g.getDelegateId()).findFirst().isPresent()) {
            return current;
        }
        for(PyroFolderDB pf:current.innerFolders) {
            PyroFolderDB result = getParentFolder(pf,g);
            if(result != null) {
                return result;
            }
        }
        return null;
    }
}

