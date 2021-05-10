package entity.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity(name="entity_core_pyrofolder")
public class PyroFolderDB extends entity.core.PyroFileContainerDB {
	
	public String name;
	
	@javax.persistence.ManyToOne
	public entity.core.PyroFileContainerDB parent;
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroFolderDB> innerFolders = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroBinaryFileDB> binaryFiles = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroURLFileDB> urlFiles = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroTextualFileDB> textualFiles = new java.util.ArrayList<>();

	public java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> getFiles() {
		java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> files = new java.util.ArrayList<>();	
		files.addAll(binaryFiles);
		files.addAll(urlFiles);
		files.addAll(textualFiles);
		return files;
	}

	public boolean removeFile(PanacheEntity e, boolean delete) {
		if(e instanceof entity.core.PyroBinaryFileDB) {
			binaryFiles.remove(e);
		} else if(e instanceof entity.core.PyroURLFileDB) {
			urlFiles.remove(e);
		} else if(e instanceof entity.core.PyroTextualFileDB) {
			textualFiles.remove(e);
		} else {
			return false;
		}

		if(delete) {
			e.delete();
		}

		return true;
	}
}
