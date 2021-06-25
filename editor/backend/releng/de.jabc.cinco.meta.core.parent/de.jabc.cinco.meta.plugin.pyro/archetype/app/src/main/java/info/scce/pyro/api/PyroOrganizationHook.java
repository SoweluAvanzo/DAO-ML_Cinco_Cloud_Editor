package info.scce.pyro.api;


import java.util.Set;

/**
 * Author zweihoff
 */
public abstract class PyroOrganizationHook extends PyroHook {



    public abstract void execute(entity.core.PyroOrganizationDB organization);

}

