package info.scce.cincocloud.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroModelFile extends PyroFile {
    private boolean isPublic;

    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
    public boolean getisPublic() {
        return this.isPublic;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
    public void setisPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }
}


