package info.scce.cincocloud.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.util.Constants;

public class RESTBaseImpl implements RESTBaseType {

  private long id;
  private String runtimeType;

  @JsonProperty(Constants.PYRO_ID)
  public long getId() {
    return this.id;
  }

  @JsonProperty(Constants.PYRO_ID)
  public void setId(final long id) {
    this.id = id;
  }

  @JsonProperty(Constants.PYRO_RUNTIME_TYPE)
  public String getRuntimeType() {
    return this.runtimeType;
  }

  @JsonProperty(Constants.PYRO_RUNTIME_TYPE)
  public void setRuntimeType(final String runtimeType) {
    this.runtimeType = runtimeType;
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