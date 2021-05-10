package entity.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroBinaryFileDB extends PanacheEntity {
	
    public String filename;
    public String extension;
    @javax.persistence.OneToOne(cascade=javax.persistence.CascadeType.ALL)
    public entity.core.BaseFileDB file;
    @javax.persistence.ManyToOne
    public entity.core.PyroFileContainerDB parent;
}