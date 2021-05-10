package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroFile extends info.scce.pyro.rest.RESTBaseImpl implements IPyroFile
{

    private String filename;

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public String getfilename() {
        return this.filename;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public void setfilename(final String filename) {
        this.filename = filename;
    }
    
    private String extension;

    @com.fasterxml.jackson.annotation.JsonProperty("extension")
    public String getextension() {
        return this.extension;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("extension")
    public void setextension(final String extension) {
        this.extension = extension;
    }

    private String __type;

    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public String get__type() {
        return this.__type;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public void set__type(final String __type) {
        this.__type = __type;
    }

}