package info.scce.pyro.sync;

@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
public class UpdateCursorPosition {
	
	private long graphModelId;

    @com.fasterxml.jackson.annotation.JsonProperty("graphModelId")
    public long getgraphModelId() {
        return this.graphModelId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("graphModelId")
    public void setgraphModelId(final long graphModelId) {
        this.graphModelId = graphModelId;
    }
    

    private double x;
    
    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public double getx() {
        return this.x;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public void setx(final double x) {
        this.x = x;
    }
    
    
    private double y;
    
    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public double gety() {
        return this.y;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public void sety(final double y) {
        this.y = y;
    }

}
