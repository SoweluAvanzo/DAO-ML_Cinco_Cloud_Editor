package entity.externallibrary;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_externallibrary_externalactivity")
public class ExternalActivityDB extends PanacheEntity {
	
	public String name;
	
	public String description;
	
	@Override
	public void delete() {
		
		// delete entity
		super.delete();
	}
}
