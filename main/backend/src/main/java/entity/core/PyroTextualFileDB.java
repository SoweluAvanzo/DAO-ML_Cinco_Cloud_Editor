package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroTextualFileDB extends PanacheEntity {
    
    public String filename;
    public String extension;
    public String content;

    @javax.persistence.ManyToOne
    public entity.core.PyroFileContainerDB parent;
}