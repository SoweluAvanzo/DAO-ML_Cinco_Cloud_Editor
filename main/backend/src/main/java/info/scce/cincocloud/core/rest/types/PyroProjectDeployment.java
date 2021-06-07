package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.rest.RESTBaseImpl;

public class PyroProjectDeployment extends RESTBaseImpl {

    private String url;

    private PyroProjectDeploymentStatus status;

    public PyroProjectDeployment() {
    }

    public PyroProjectDeployment(String url, PyroProjectDeploymentStatus status) {
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PyroProjectDeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(PyroProjectDeploymentStatus status) {
        this.status = status;
    }
}
