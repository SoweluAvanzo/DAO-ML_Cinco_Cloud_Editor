package info.scce.cincocloud.core.rest.types;

/**
 * Author zweihoff
 */

public class CreatePyroURLFile {

    private long parentId;
    private String filename;
    private String extension;
    private String url;

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public long getparentId() {
        return this.parentId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parentId")
    public void setparentId(final long parentId) {
        this.parentId = parentId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public String getfilename() {
        return this.filename;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public void setfilename(final String filename) {
        this.filename = filename;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("extension")
    public String getextension() {
        return this.extension;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("extension")
    public void setextension(final String extension) {
        this.extension = extension;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public String geturl() {
        return this.url;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public void seturl(final String url) {
        this.url = url;
    }
}