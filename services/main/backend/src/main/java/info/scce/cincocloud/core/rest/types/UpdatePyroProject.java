package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePyroProject {

  private long projectId = -1;
  private long userId = -1;

  @JsonProperty("projectId")
  public long getprojectId() {
    return this.projectId;
  }

  @JsonProperty("projectId")
  public void setprojectId(final long projectId) {
    this.projectId = projectId;
  }

  @JsonProperty("userId")
  public long getuserId() {
    return this.userId;
  }

  @JsonProperty("userId")
  public void setuserId(final long userId) {
    this.userId = userId;
  }
}