package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class CreatePyroFolder {
	
    private long parentId;

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public long getparentId() {
        return this.parentId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public void setparentId(final long parentId) {
        this.parentId = parentId;
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
}