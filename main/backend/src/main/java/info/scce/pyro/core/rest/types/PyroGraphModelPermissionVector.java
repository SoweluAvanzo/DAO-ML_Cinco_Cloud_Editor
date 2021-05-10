package info.scce.pyro.core.rest.types;

public class PyroGraphModelPermissionVector extends info.scce.pyro.rest.RESTBaseImpl {
	
	private PyroUser user;

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public PyroUser getuser() {
        return this.user;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public void setuser(final PyroUser user) {
        this.user = user;
    }
    
    private PyroProject project;    
    
    @com.fasterxml.jackson.annotation.JsonProperty("project")
    public PyroProject getproject() {
        return this.project;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("project")
    public void setproject(final PyroProject project) {
        this.project = project;
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

        result.setuser(PyroUser.fromEntity(entity.user, objectCache));
        result.setproject(PyroProject.fromEntity(entity.project, objectCache));
        result.setgraphModelType(entity.graphModelType);

        for(entity.core.PyroCrudOperationDB ar : entity.permissions){
            result.getpermissions().add(ar);
        }

        return result;
    }
}