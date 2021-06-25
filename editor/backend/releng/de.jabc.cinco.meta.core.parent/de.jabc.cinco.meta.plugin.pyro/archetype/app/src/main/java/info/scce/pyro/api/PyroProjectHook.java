package info.scce.pyro.api;

import entity.core.PyroProjectDB;

/**
 * Author zweihoff
 */
public abstract class PyroProjectHook extends PyroHook {

    public abstract void execute(PyroProjectDB project);
    
    public boolean canExecute(PyroProjectDB project) { return true; }
}

