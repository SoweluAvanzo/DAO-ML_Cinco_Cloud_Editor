package info.scce.pyro.api;

/**
 * Author zweihoff
 */
public abstract class PyroProjectService<T extends io.quarkus.hibernate.orm.panache.PanacheEntity> extends PyroHook{

    public abstract boolean isValid(java.util.Map<String,String> inputs,java.util.List<T> services);
    
    public boolean canExecute(java.util.List<T> services) { return true; };
    
    public boolean isDisabled(java.util.List<T> services) { return false; };
    
    public abstract void execute(T serviceData);
}
