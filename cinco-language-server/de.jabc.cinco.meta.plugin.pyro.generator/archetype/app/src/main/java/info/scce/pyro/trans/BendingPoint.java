package info.scce.pyro.trans;

/**
 * Author zweihoff
 */

public class BendingPoint implements graphmodel.BendingPoint
{
    private long x;
    private long y;
    
    public BendingPoint(long x,long y) {
    	this.x = x;
    	this.y = y;
    }
    
    @Override
    public long getX() {
        return this.x;
    }

    @Override
    public long getY() {
        return this.y;
    }
}
