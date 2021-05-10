package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

public class ResizeNodeCommand extends Command{
    
	@com.fasterxml.jackson.annotation.JsonProperty("oldWidth")
    long oldWidth;
    
	@com.fasterxml.jackson.annotation.JsonProperty("width")
    long width;
    
	@com.fasterxml.jackson.annotation.JsonProperty("oldHeight")
    long oldHeight;
    
	@com.fasterxml.jackson.annotation.JsonProperty("height")
    long height;

    public long getOldWidth() {
        return oldWidth;
    }

    public void setOldWidth(long oldWidth) {
        this.oldWidth = oldWidth;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getOldHeight() {
        return oldHeight;
    }

    public void setOldHeight(long oldHeight) {
        this.oldHeight = oldHeight;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    @Override
    protected void rewrite(long oldId, long newId) {
        
    }
}