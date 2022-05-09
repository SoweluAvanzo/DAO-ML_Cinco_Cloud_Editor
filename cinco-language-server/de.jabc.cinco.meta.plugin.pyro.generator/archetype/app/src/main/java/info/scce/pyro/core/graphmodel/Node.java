package info.scce.pyro.core.graphmodel;


/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public interface Node extends ModelElement {
	
    @com.fasterxml.jackson.annotation.JsonProperty("a_x")
    public long getx();

    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public void setx(final long x);

    @com.fasterxml.jackson.annotation.JsonProperty("a_y")
    public long gety();

    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public void sety(final long y);

    @com.fasterxml.jackson.annotation.JsonProperty("a_angle")
    public long getangle();
    @com.fasterxml.jackson.annotation.JsonProperty("angle")
    public void setangle(final long angle);
    @com.fasterxml.jackson.annotation.JsonProperty("a_width")
    public long getwidth();

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public void setwidth(final long width);

    @com.fasterxml.jackson.annotation.JsonProperty("a_height")
    public long getheight();
    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public void setheight(final long height);
    @com.fasterxml.jackson.annotation.JsonProperty("a_incoming")
    public java.util.List<Edge> getincoming();

    @com.fasterxml.jackson.annotation.JsonProperty("incoming")
    public void setincoming(final java.util.List<Edge> incoming);

    @com.fasterxml.jackson.annotation.JsonProperty("a_outgoing")
    public java.util.List<Edge> getoutgoing();

    @com.fasterxml.jackson.annotation.JsonProperty("outgoing")
    public void setoutgoing(final java.util.List<Edge> outgoing);
}