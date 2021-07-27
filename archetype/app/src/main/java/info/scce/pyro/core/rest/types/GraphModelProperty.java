package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */


public class GraphModelProperty extends info.scce.pyro.rest.RESTBaseImpl
{

    private boolean isPublic;

    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
    public boolean getisPublic() {
        return this.isPublic;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
    public void setisPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }

    private String connector;

    @com.fasterxml.jackson.annotation.JsonProperty("connector")
    public String getconnector() {
        return this.connector;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("connector")
    public void setconnector(final String connector) {
        this.connector = connector;
    }

    private long width;

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public long getwidth() {
        return this.width;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public void setwidth(final long width) {
        this.width = width;
    }

    private long height;

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public long getheight() {
        return this.height;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public void setheight(final long height) {
        this.height = height;
    }

    private double scale;

    @com.fasterxml.jackson.annotation.JsonProperty("scale")
    public double getscale() {
        return this.scale;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("scale")
    public void setscale(final double scale) {
        this.scale = scale;
    }

    private String messageType;

    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
    public String getmessageType() {
        return this.messageType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
    public void setmessageType(final String messageType) {
        this.messageType = messageType;
    }
    
    private String router;

    @com.fasterxml.jackson.annotation.JsonProperty("router")
    public String getrouter() {
        return this.router;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("router")
    public void setrouter(final String router) {
        this.router = router;
    }
    
    public static GraphModelProperty fromEntity(
    		final long id,
    		final String runtimeType,
    		final String connector,
    		final String router,
    		final long width,
    		final long height,
    		final double scale,
    		final boolean isPublic
    		) {
    	final GraphModelProperty result = new GraphModelProperty();
        
        result.setId(id);
        result.setRuntimeType(runtimeType);
        result.setconnector(connector);
        result.setrouter(router);
        result.setwidth(width);
        result.setheight(height);
        result.setscale(scale);
        result.setisPublic(isPublic);
        result.setmessageType("graphmodel");

        return result;
    }
}
