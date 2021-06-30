package info.scce.pyro.core.command.types;

import info.scce.pyro.core.rest.types.PyroProject;

/**
 * Author zweihoff
 */

public class ProjectMessage extends Message {
	
    @com.fasterxml.jackson.annotation.JsonProperty("project")
    private PyroProject project;

    public ProjectMessage() {
        super();
        super.setMessageType("project");
    }

    public PyroProject getProject() {
        return project;
    }

    public void setProject(PyroProject project) {
        this.project = project;
    }
}