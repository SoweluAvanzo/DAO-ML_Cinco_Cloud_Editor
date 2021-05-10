package entity.hierarchy;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hierarchy_ta")
public class TADB extends PanacheEntity {
	
	public String ofTA;
	
	public String ofTB;
	
	public String ofTC;
	
	public String ofTD;
}
