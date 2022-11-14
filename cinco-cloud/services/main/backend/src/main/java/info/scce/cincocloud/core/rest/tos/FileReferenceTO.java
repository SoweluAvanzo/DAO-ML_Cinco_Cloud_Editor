package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class FileReferenceTO extends RESTBaseImpl {

  private long id;
  private String fileName;
  private String contentType;

  public FileReferenceTO() {
  }

  public FileReferenceTO(final BaseFileDB delegate) {
    this.setId(delegate.id);
    if (delegate.fileExtension != null) {
      this.setFileName(delegate.filename + "." + delegate.fileExtension);
    } else {
      this.setFileName(delegate.filename);
    }
    this.setContentType(delegate.contentType);
  }

  @JsonProperty("id")
  public long getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(long id) {
    this.id = id;
  }

  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }

  @JsonProperty("fileName")
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @JsonProperty("contentType")
  public String getContentType() {
    return contentType;
  }

  @JsonProperty("contentType")
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof FileReferenceTO)) {
      return false;
    }

    final FileReferenceTO that = (FileReferenceTO) obj;
    if (this.getId() == -1 && that.getId() == -1) {
      return false;
    }

    return this.getId() == that.getId();
  }
}
