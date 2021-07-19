package info.scce.cincocloud.mq;

public class WorkspaceImageBuildResult {

    /**
     * The ID of the project.
     * Should match with the ID of the corresponding build job {@link WorkspaceImageBuildJob}.
     */
    public Long projectId;

    /**
     * The the image could be built successfully.
     */
    public Boolean success;

    /**
     * A message that contains e.g. an error message if {@link #success} is false.
     */
    public String message;

    /**
     * The name of the built image.
     */
    public String image;
}
