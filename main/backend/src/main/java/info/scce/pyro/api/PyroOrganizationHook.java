package info.scce.pyro.api;


/**
 * Author zweihoff
 */
public abstract class PyroOrganizationHook extends PyroHook {



    public abstract void execute(entity.core.PyroOrganizationDB organization);

}

