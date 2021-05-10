package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.BaseFileDB;

public class FileReference {

    private long id;
    private String fileName;
    private String contentType;

    public FileReference() {
    }

    public FileReference(final BaseFileDB delegate) {
        this.setId(delegate.id);
        if (delegate.fileExtension != null) {
            this.setFileName(delegate.filename + "." + delegate.fileExtension);
        } else {
            this.setFileName(delegate.filename);
        }
        this.setContentType(delegate.contentType);
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public long getId() {
        return id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FileReference)) {
            return false;
        }

        final FileReference that = (FileReference) obj;
        if (this.getId() == -1 && that.getId() == -1) {
            return false;
        }

        return this.getId() == that.getId();
    }
}