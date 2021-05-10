package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroTextualFileDB extends PanacheEntity {

    public String filename;
    public String extension;
    public String content;

    @javax.persistence.ManyToOne
    public PyroFileContainerDB parent;
}