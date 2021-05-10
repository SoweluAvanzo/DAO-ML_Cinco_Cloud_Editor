package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroFolder extends info.scce.pyro.rest.RESTBaseImpl
{
    private String name;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final String name) {
        this.name = name;
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

    private java.util.List<PyroFile> files = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("files")
    public java.util.List<PyroFile> getfiles() {
        return this.files;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("files")
    public void setfiles(final java.util.List<PyroFile> files) {
        this.files = files;
    }

    public static PyroFolder fromEntity(final entity.core.PyroFolderDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        final PyroFolder result;
        result = new PyroFolder();
        result.setId(entity.id);

        result.setname(entity.name);

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
        	
        objectCache.putRestTo(entity, result);

        return result;
    }
}

