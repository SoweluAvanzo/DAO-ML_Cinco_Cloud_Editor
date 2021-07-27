package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroUser extends info.scce.pyro.rest.RESTBaseImpl {   
    
    private java.util.List<PyroProject> ownedProjects = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("ownedProjects")
    public java.util.List<PyroProject> getownedProjects() {
        return this.ownedProjects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("ownedProjects")
    public void setownedProjects(final java.util.List<PyroProject> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }
    
    private java.util.List<entity.core.PyroSystemRoleDB> systemRoles = new java.util.LinkedList<>();
    
    @com.fasterxml.jackson.annotation.JsonProperty("systemRoles")
    public java.util.List<entity.core.PyroSystemRoleDB> getsystemRoles() {
        return this.systemRoles;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("systemRoles")
    public void setsystemRoles(final java.util.List<entity.core.PyroSystemRoleDB> systemRoles) {
        this.systemRoles = systemRoles;
    }
    
    private java.lang.String username;

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public java.lang.String getusername() {
        return this.username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public void setusername(final java.lang.String username) {
        this.username = username;
    }
    
    private java.lang.String email;

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public java.lang.String getemail() {
        return this.email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public void setemail(final java.lang.String email) {
        this.email = email;
    }
    
    private FileReference profilePicture;

    @com.fasterxml.jackson.annotation.JsonProperty("profilePicture")
    public FileReference getprofilePicture() {
        return this.profilePicture;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("profilePicture")
    public void setprofilePicture(final FileReference profilePicture) {
        this.profilePicture = profilePicture;
    }

    public static PyroUser fromEntity(final entity.core.PyroUserDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        final PyroUser result;
        result = new PyroUser();
        result.setId(entity.id);

        result.setemail(entity.email);
        result.setusername(entity.username);
        
        if (entity.profilePicture != null) {
        	result.setprofilePicture(new FileReference(entity.profilePicture));
        }

        objectCache.putRestTo(entity, result);

        for(entity.core.PyroProjectDB p:entity.ownedProjects){
            result.getownedProjects().add(PyroProject.fromEntity(p,objectCache));
        }

        for(entity.core.PyroSystemRoleDB p:entity.systemRoles){
            result.getsystemRoles().add(p);
        }

        return result;
    }
}