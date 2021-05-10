package entity.hooksandactions;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_hooksandactions_hookatype")
public class HookATypeDB extends PanacheEntity {
	
	public String attribute;
}
