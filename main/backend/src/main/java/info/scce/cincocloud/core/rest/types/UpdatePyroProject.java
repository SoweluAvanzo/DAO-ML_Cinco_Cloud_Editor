package info.scce.cincocloud.core.rest.types;

/**
 * Author zweihoff
 */

public class UpdatePyroProject {

    private long projectId = -1;
    private long userId = -1;

    @com.fasterxml.jackson.annotation.JsonProperty("projectId")
    public long getprojectId() {
        return this.projectId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("projectId")
    public void setprojectId(final long projectId) {
        this.projectId = projectId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("userId")
    public long getuserId() {
        return this.userId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("userId")
    public void setuserId(final long userId) {
        this.userId = userId;
    }
}