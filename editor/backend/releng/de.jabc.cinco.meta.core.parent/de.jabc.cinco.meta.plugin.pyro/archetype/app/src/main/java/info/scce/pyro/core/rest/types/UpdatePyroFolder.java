package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class UpdatePyroFolder {

    private long id;

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public long getId() {
        return this.id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(final long id) {
        this.id = id;
    }

    private String name;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final String name) {
        this.name = name;
    }
}