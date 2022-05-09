package de.jabc.cinco.meta.runtime.action;

import graphmodel.IdentifiableElement;
import info.scce.pyro.core.command.CommandExecuter;

import javax.ws.rs.core.UriInfo;

/**
 * Author zweihoff
 */
public abstract class CincoCustomAction<T extends IdentifiableElement> extends info.scce.pyro.api.PyroControl {

    private UriInfo uriInfo;

    public final void init(CommandExecuter cmdExecuter, UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        super.init(cmdExecuter);
    }

    public UriInfo getUriInfo() { return uriInfo; }

    public String getName() {
        return getClass().getName();
    }

    public boolean hasDoneChanges() {
        return true;
    }

    public boolean canExecute(T element) {
        return true;
    }

    public abstract void execute(T element);
}