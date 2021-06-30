package info.scce.pyro.core.rest.types;

public class PyroOrganizationAccessRightVector extends info.scce.pyro.rest.RESTBaseImpl {

	private java.util.List<entity.core.PyroOrganizationAccessRightDB> accessRights = new java.util.LinkedList<>();
    
    @com.fasterxml.jackson.annotation.JsonProperty("accessRights")
    public java.util.List<entity.core.PyroOrganizationAccessRightDB> getaccessRights() {
        return this.accessRights;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("accessRights")
    public void setaccessRights(final java.util.List<entity.core.PyroOrganizationAccessRightDB> accessRights) {
        this.accessRights = accessRights;
    }    
    
    private PyroUser user;

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public PyroUser getuser() {
        return this.user;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public void setuser(final PyroUser user) {
        this.user = user;
    }
    
    private PyroOrganization organization;

    @com.fasterxml.jackson.annotation.JsonProperty("organization")
    public PyroOrganization getorganization() {
        return this.organization;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("organization")
    public void setorganization(final PyroOrganization organization) {
        this.organization = organization;
    }
    
    public static PyroOrganizationAccessRightVector fromEntity(
    		final entity.core.PyroOrganizationAccessRightVectorDB entity, 
    		final info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        
        final PyroOrganizationAccessRightVector result;
        result = new PyroOrganizationAccessRightVector();
        result.setId(entity.id);

        result.setuser(PyroUser.fromEntity(entity.user, objectCache));
        result.setorganization(PyroOrganization.fromEntity(entity.organization, objectCache));

        objectCache.putRestTo(entity, result);

        for(entity.core.PyroOrganizationAccessRightDB ar : entity.accessRights){
            result.getaccessRights().add(ar);
        }

        return result;
    }
}