package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroURLFileDB extends PanacheEntity {
    
    public String filename;
    public String extension;
    public String url;
    
    @javax.persistence.ManyToOne
    public entity.core.PyroFileContainerDB parent;
}