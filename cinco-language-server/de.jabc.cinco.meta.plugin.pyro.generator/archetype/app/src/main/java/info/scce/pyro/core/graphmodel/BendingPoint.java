package info.scce.pyro.core.graphmodel;

/**
 * Author zweihoff
 */

public class BendingPoint {
    
	private long x;

    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public long getx() {
        return this.x;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public void setx(final long x) {
        this.x = x;
    }

    private long y;

    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public long gety() {
        return this.y;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public void sety(final long y) {
        this.y = y;
    }

    public static BendingPoint fromEntity(final entity.core.BendingPointDB entity) {

        final BendingPoint result = new BendingPoint();
        result.setx(entity.x);
        result.sety(entity.y);

        return result;
    }
}