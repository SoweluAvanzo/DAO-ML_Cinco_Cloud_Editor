package info.scce.pyro.core.graphmodel;

import info.scce.pyro.core.rest.types.IPyroFile;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public interface GraphModel extends ModelElementContainer, IPyroFile {
    public Double getscale();

    @com.fasterxml.jackson.annotation.JsonProperty("scale")
    public void setscale(final Double scale);

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public Long getwidth();

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public void setwidth(final Long width);
    
    @com.fasterxml.jackson.annotation.JsonProperty("a_height")
    public Long getheight();

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public void setheight(final Long height);

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public String getfilename();

    @com.fasterxml.jackson.annotation.JsonProperty("filename")
    public void setfilename(final String filename);

    @com.fasterxml.jackson.annotation.JsonProperty("extension")
    public String getextension();

    @com.fasterxml.jackson.annotation.JsonProperty("extension")
    public void setextension(final String extension);
    
    @com.fasterxml.jackson.annotation.JsonProperty("a_router")
    public String getrouter();

    @com.fasterxml.jackson.annotation.JsonProperty("router")
    public void setrouter(final String router);

    @com.fasterxml.jackson.annotation.JsonProperty("a_connector")
    public String getconnector();

    @com.fasterxml.jackson.annotation.JsonProperty("connector")
    public void setconnector(final String connector);
    
    @com.fasterxml.jackson.annotation.JsonProperty("a_isPublic")
    public boolean getisPublic();

    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
    public void setisPublic(final boolean isPublic);

}