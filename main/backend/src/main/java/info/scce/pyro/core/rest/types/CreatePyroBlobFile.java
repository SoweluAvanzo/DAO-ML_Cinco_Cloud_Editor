package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class CreatePyroBlobFile
{
    private long parentId;

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public long getparentId() {
        return this.parentId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public void setparentId(final long parentId) {
        this.parentId = parentId;
    }

    private String file;

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public String getfile() {
        return this.file;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public void setfile(final String file) {
        this.file = file;
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
