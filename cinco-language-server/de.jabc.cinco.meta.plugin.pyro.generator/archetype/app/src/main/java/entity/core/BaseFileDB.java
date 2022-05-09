package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class BaseFileDB extends PanacheEntity {
    
    public String filename;
    public String fileExtension;
    public String path;
    public String contentType;
    
    public String getFileName() {
    	return fileExtension == null ? 
				filename
				: filename + "." + fileExtension;
    }
}