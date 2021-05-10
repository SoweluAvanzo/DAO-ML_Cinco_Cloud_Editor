package info.scce.pyro.core.rest.types;

public class PyroOrganization extends info.scce.pyro.rest.RESTBaseImpl {
	
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
      
    private java.util.List<PyroUser> owners = new java.util.LinkedList<>();;

    @com.fasterxml.jackson.annotation.JsonProperty("owners")
    public java.util.List<PyroUser> getowners() {
        return this.owners;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owners")
    public void setowners(final java.util.List<PyroUser> owners) {
        this.owners = owners;
    }
    
    private java.util.List<PyroUser> members = new java.util.LinkedList<>();;

    @com.fasterxml.jackson.annotation.JsonProperty("members")
    public java.util.List<PyroUser> getmembers() {
        return this.members;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("members")
    public void setmembers(final java.util.List<PyroUser> members) {
        this.members = members;
    }
    
    private java.util.List<PyroProject> projects = new java.util.LinkedList<>();;

    @com.fasterxml.jackson.annotation.JsonProperty("projects")
    public java.util.List<PyroProject> getprojects() {
        return this.projects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("projects")
    public void setprojects(final java.util.List<PyroProject> projects) {
        this.projects = projects;
    }
    
    private PyroStyle style;

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public PyroStyle getstyle() {
        return this.style;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public void setstyle(final PyroStyle style) {
        this.style = style;
    }

    public static PyroOrganization fromEntity(
    		final entity.core.PyroOrganizationDB entity, 
    		final info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        
        final PyroOrganization result;
        result = new PyroOrganization();
        result.setId(entity.id);

        result.setname(entity.name);
        result.setdescription(entity.description);
        result.setstyle(PyroStyle.fromEntity(entity.style, objectCache));
        objectCache.putRestTo(entity, result);
                
        for(entity.core.PyroUserDB o : entity.owners){
            result.getowners().add(PyroUser.fromEntity(o, objectCache));
        }

        for(entity.core.PyroUserDB m : entity.members){
            result.getmembers().add(PyroUser.fromEntity(m, objectCache));
        }
        
        for(entity.core.PyroProjectDB p : entity.projects){
            result.getprojects().add(PyroProject.fromEntity(p, objectCache));
        }

        return result;
    }
}