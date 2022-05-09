package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

public class HighlightCommand {
	
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    long id;

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_r")
    long foregroundColorR;
    
    @com.fasterxml.jackson.annotation.JsonProperty("foreground_g")
    long foregroundColorG;
    
    @com.fasterxml.jackson.annotation.JsonProperty("foreground_b")
    long foregroundColorB;

    @com.fasterxml.jackson.annotation.JsonProperty("background_r")
    long backgroundColorR;
    
    @com.fasterxml.jackson.annotation.JsonProperty("background_g")
    long backgroundColorG;
    
    @com.fasterxml.jackson.annotation.JsonProperty("background_b")
    long backgroundColorB;

    @com.fasterxml.jackson.annotation.JsonProperty("lightType")
    String lightType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getForegroundColorR() {
        return foregroundColorR;
    }

    public void setForegroundColorR(long foregroundColorR) {
        this.foregroundColorR = foregroundColorR;
    }

    public long getForegroundColorG() {
        return foregroundColorG;
    }

    public void setForegroundColorG(long foregroundColorG) {
        this.foregroundColorG = foregroundColorG;
    }

    public long getForegroundColorB() {
        return foregroundColorB;
    }

    public void setForegroundColorB(long foregroundColorB) {
        this.foregroundColorB = foregroundColorB;
    }

    public long getBackgroundColorR() {
        return backgroundColorR;
    }

    public void setBackgroundColorR(long backgroundColorR) {
        this.backgroundColorR = backgroundColorR;
    }

    public long getBackgroundColorG() {
        return backgroundColorG;
    }

    public void setBackgroundColorG(long backgroundColorG) {
        this.backgroundColorG = backgroundColorG;
    }

    public long getBackgroundColorB() {
        return backgroundColorB;
    }

    public void setBackgroundColorB(long backgroundColorB) {
        this.backgroundColorB = backgroundColorB;
    }

    public String getLightType() {
        return lightType;
    }

    public void setLightType(String lightType) {
        this.lightType = lightType;
    }
}