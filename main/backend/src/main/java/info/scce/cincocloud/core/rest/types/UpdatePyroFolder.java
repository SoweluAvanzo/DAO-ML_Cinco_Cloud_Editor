package info.scce.cincocloud.core.rest.types;

public class UpdatePyroFolder {

    private long id;
    private String name;

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public long getId() {
        return this.id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(final long id) {
        this.id = id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final String name) {
        this.name = name;
    }
}