package info.scce.cincocloud.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.util.Constants;

public class RESTBaseImpl implements RESTBaseType {

  private long id;

  @JsonProperty(Constants.CINCO_CLOUD_ID_PROPERTY_NAME)
  public long getId() {
    return this.id;
  }

  @JsonProperty(Constants.CINCO_CLOUD_ID_PROPERTY_NAME)
  public void setId(final long id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    return java.lang.Math.toIntExact(this.getId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(getClass().isInstance(obj))) {
      return false;
    }

    final RESTBaseType that = (RESTBaseType) obj;
    if (this.getId() >= 0 && that.getId() >= 0) {
      return this.getId() == that.getId();
    }

    return false;
  }
}
