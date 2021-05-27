package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class PyroProjectDeployment extends RESTBaseImpl {

    private String url;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }
}
