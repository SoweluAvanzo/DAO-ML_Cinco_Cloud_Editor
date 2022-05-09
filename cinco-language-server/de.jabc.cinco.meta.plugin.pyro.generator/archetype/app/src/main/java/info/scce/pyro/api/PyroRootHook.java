package info.scce.pyro.api;


import java.util.Set;

/**
 * Author zweihoff
 */
public abstract class PyroRootHook extends PyroHook {


    public abstract void execute(entity.core.PyroSettingsDB setting);

}

