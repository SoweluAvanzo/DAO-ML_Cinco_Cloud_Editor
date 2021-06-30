package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class CreatePyroBinaryFile {
	
    private long parentId;

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public long getparentId() {
        return this.parentId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public void setparentId(final long parentId) {
        this.parentId = parentId;
    }

    private FileReference file;

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public FileReference getfile() {
        return this.file;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("file")
    public void setfile(final FileReference file) {
        this.file = file;
    }
}