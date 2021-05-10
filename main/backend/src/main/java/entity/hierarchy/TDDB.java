package entity.hierarchy;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hierarchy_td")
public class TDDB extends PanacheEntity {
	
	public String ofTD;
}
