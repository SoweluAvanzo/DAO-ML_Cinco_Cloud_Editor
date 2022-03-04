package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.GitInformationDB;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class GitInformationTO extends RESTBaseImpl {

    private CincoCloudProtos.GetGitInformationReply.Type type;
    private String repositoryUrl;
    private String username;
    private String password;
    private String branch;
    private String genSubdirectory;
    private long projectId;

    public static GitInformationTO fromEntity(final GitInformationDB entity) {

        final var result = new GitInformationTO();
        result.setId(entity.id);
        result.setType(entity.type);
        result.setRepositoryUrl(entity.repositoryUrl);
        result.setUsername(entity.username);
        result.setPassword(entity.password);
        result.setBranch(entity.branch);
        result.setGenSubdirectory(entity.genSubdirectory);
        result.setProjectId(entity.project.id);

        return result;
    }

    @JsonProperty("type")
    public CincoCloudProtos.GetGitInformationReply.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(CincoCloudProtos.GetGitInformationReply.Type type) {
        this.type = type;
    }

    @JsonProperty("repositoryUrl")
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    @JsonProperty("repositoryUrl")
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("branch")
    public String getBranch() {
        return branch;
    }

    @JsonProperty("branch")
    public void setBranch(String branch) {
        this.branch = branch;
    }

    @JsonProperty("genSubdirectory")
    public String getGenSubdirectory() {
        return genSubdirectory;
    }

    @JsonProperty("genSubdirectory")
    public void setGenSubdirectory(String genSubdirectory) {
        this.genSubdirectory = genSubdirectory;
    }

    @JsonProperty("projectId")
    public long getProjectId() {
        return projectId;
    }

    @JsonProperty("projectId")
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
