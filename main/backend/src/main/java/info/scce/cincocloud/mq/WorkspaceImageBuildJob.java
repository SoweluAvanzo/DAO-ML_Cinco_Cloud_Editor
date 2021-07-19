package info.scce.cincocloud.mq;

public class WorkspaceImageBuildJob {

    /**
     * The ID of the project.
     */
    public Long projectId;

    /**
     * The username the workspace belongs to.
     */
    public String username;

    /**
     * The name of the language for which a workspace image should be build.
     */
    public String language;

    public WorkspaceImageBuildJob(Long projectId, String username, String language) {
        this.projectId = projectId;
        this.username = username;
        this.language = language;
    }

    @Override
    public String toString() {
        return "WorkspaceImageBuildJob{"
                + "projectId=" + projectId
                + ", username='" + username + '\''
                + ", language='" + language + '\''
                + '}';
    }
}
