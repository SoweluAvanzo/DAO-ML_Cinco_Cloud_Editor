package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity(name = "entity_core_pyroproject")
public class PyroProjectDB extends PyroFileContainerDB {

    public String name;

    public boolean isPublic;

    public String description;

    @javax.persistence.ManyToOne
    @javax.persistence.JoinColumn(name = "owner_PyroUserDB_id")
    public PyroUserDB owner;

    @javax.persistence.ManyToOne
    @javax.persistence.JoinColumn(name = "organization_PyroOrganizationDB_id")
    public PyroOrganizationDB organization;

    @javax.persistence.OneToMany(mappedBy = "parent")
    public java.util.Collection<PyroFolderDB> innerFolders = new java.util.ArrayList<>();

    @javax.persistence.OneToMany(mappedBy = "parent")
    public java.util.Collection<PyroBinaryFileDB> binaryFiles = new java.util.ArrayList<>();

    @javax.persistence.OneToMany(mappedBy = "parent")
    public java.util.Collection<PyroURLFileDB> urlFiles = new java.util.ArrayList<>();

    @javax.persistence.OneToMany(mappedBy = "parent")
    public java.util.Collection<PyroTextualFileDB> textualFiles = new java.util.ArrayList<>();

    public java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> getFiles() {
        java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> files = new java.util.ArrayList<>();
        files.addAll(binaryFiles);
        files.addAll(urlFiles);
        files.addAll(textualFiles);
        return files;
    }

    public boolean removeFile(PanacheEntity e, boolean delete) {
        if (e instanceof PyroBinaryFileDB) {
            binaryFiles.remove(e);
        } else if (e instanceof PyroURLFileDB) {
            urlFiles.remove(e);
        } else if (e instanceof PyroTextualFileDB) {
            textualFiles.remove(e);
        } else {
            return false;
        }

        if (delete) {
            e.delete();
        }

        return true;
    }
}
