package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroProjectStructure extends PyroProject
{

    private PyroUser owner = new PyroUser();

    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    public PyroUser geowner() {
        return this.owner;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    public void setowner(final PyroUser owner) {
        this.owner = owner;
    }

    private java.util.List<PyroFolder> innerFolders = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("innerFolders")
    public java.util.List<PyroFolder> getinnerFolders() {
        return this.innerFolders;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("innerFolders")
    public void setinnerFolders(final java.util.List<PyroFolder> innerFolders) {
        this.innerFolders = innerFolders;
    }
    
    private java.util.List<IPyroFile> files = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("files")
    public java.util.List<IPyroFile> getfiles() {
        return this.files;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("files")
    public void setfiles(final java.util.List<IPyroFile> files) {
        this.files = files;
    }
    
    private String name;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final String name) {
        this.name = name;
    }
    
    private String description;

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public String getdescription() {
        return this.description;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public void setdescription(final String description) {
        this.description = description;
    }
    
    public static PyroProjectStructure fromEntity(final entity.core.PyroProjectDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		
        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        final PyroProjectStructure result;
        result = new PyroProjectStructure();
        result.setId(entity.id);

        result.setname(entity.name);
        result.setdescription(entity.description);

        objectCache.putRestTo(entity, result);

        result.setowner(PyroUser.fromEntity(entity.owner,objectCache));
		
		for(entity.core.PyroFolderDB p:entity.innerFolders){
	            result.getinnerFolders().add(PyroFolder.fromEntity(p,objectCache));
	    }
	
		for(entity.core.PyroBinaryFileDB p:entity.binaryFiles){
		    result.getfiles().add(PyroBinaryFile.fromEntity(p,objectCache));  
		}
		for(entity.core.PyroURLFileDB p:entity.urlFiles){
		    result.getfiles().add(PyroURLFile.fromEntity(p,objectCache));  
		}
		for(entity.core.PyroTextualFileDB p:entity.textualFiles){
		    result.getfiles().add(PyroTextualFile.fromEntity(p,objectCache));  
		}

	    if(entity.organization != null) {
	       result.setorganization(PyroOrganization.fromEntity(entity.organization,objectCache));
	    }

        return result;
    }
}
