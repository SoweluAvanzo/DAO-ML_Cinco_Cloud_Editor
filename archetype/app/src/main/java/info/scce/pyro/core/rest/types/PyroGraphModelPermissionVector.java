package info.scce.pyro.core.rest.types;

public class PyroGraphModelPermissionVector extends info.scce.pyro.rest.RESTBaseImpl {
	
	private long user;

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public long getuser() {
        return this.user;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public void setuser(final long user) {
        this.user = user;
    }
    
    private entity.core.PyroGraphModelTypeDB graphModelType;
    
    @com.fasterxml.jackson.annotation.JsonProperty("graphModelType")
    public entity.core.PyroGraphModelTypeDB getgraphModelType() {
        return this.graphModelType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("graphModelType")
    public void setgraphModelType(final entity.core.PyroGraphModelTypeDB graphModelType) {
        this.graphModelType = graphModelType;
    }
    
    private java.util.List<entity.core.PyroCrudOperationDB> permissions = new java.util.LinkedList<>();
    
    @com.fasterxml.jackson.annotation.JsonProperty("permissions")
    public java.util.List<entity.core.PyroCrudOperationDB> getpermissions() {
        return this.permissions;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("permissions")
    public void setpermissions(final java.util.List<entity.core.PyroCrudOperationDB> permissions) {
        this.permissions = permissions;
    }
    
    public static PyroGraphModelPermissionVector fromEntity(
    		final entity.core.PyroGraphModelPermissionVectorDB entity, 
    		final info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        
        final PyroGraphModelPermissionVector result;
        result = new PyroGraphModelPermissionVector();
        result.setId(entity.id);

        result.setuser(entity.userId);
        result.setgraphModelType(entity.graphModelType);

        for(entity.core.PyroCrudOperationDB ar : entity.permissions){
            result.getpermissions().add(ar);
        }

        return result;
    }
}