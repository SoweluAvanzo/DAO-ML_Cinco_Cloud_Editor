package entity.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class BendingPointDB extends PanacheEntity implements graphmodel.BendingPoint {
    public long x;
    public long y;
    
    public long getX() {
    	return x;
    }
    
    public long getY() {
    	return y;
    }
}

