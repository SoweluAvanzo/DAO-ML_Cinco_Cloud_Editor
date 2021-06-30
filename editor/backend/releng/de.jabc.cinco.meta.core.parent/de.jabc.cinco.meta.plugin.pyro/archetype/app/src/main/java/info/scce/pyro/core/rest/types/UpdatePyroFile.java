package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class UpdatePyroFile {

    private long id;

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public long getId() {
        return this.id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(final long id) {
        this.id = id;
    }
    
    private long parentId;

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public long getparentId() {
        return this.parentId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public void setparentId(final long parentId) {
        this.parentId = parentId;
    }

    private String filename;

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public String getfilename() {
        return this.filename;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public void setfilename(final String filename) {
        this.filename = filename;
    }
}