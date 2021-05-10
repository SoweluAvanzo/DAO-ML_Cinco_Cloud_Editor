package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroURLFileDB extends PanacheEntity {

    public String filename;
    public String extension;
    public String url;

    @javax.persistence.ManyToOne
    public PyroFileContainerDB parent;
}