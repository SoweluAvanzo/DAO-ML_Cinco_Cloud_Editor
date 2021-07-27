package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroProject extends info.scce.pyro.rest.RESTBaseImpl
{

    private PyroUser owner;

    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    public PyroUser getowner() {
        return this.owner;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    public void setowner(final PyroUser owner) {
        this.owner = owner;
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
   
    private java.lang.String name;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public java.lang.String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final java.lang.String name) {
        this.name = name;
    }
    
    private java.lang.String description;

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public java.lang.String getdescription() {
        return this.description;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public void setdescription(final java.lang.String description) {
        this.description = description;
    }

    public static PyroProject fromEntity(final entity.core.PyroProjectDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        final PyroProject result;
        result = new PyroProject();
        result.setId(entity.id);

        result.setname(entity.name);
        result.setdescription(entity.description);

        objectCache.putRestTo(entity, result);


        if(entity.organization != null) {
            result.setorganization(PyroOrganization.fromEntity(entity.organization,objectCache));
        }

        if(entity.owner != null) {
            result.setowner(PyroUser.fromEntity(entity.owner,objectCache));
        }
        return result;
    }
}