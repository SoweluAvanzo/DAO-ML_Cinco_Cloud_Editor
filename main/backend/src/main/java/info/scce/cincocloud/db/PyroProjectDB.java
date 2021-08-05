package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "entity_core_pyroproject")
public class PyroProjectDB extends PanacheEntity {

    public String name;

    public String description;

    @OneToOne
    public PyroWorkspaceImageDB image;

    @ManyToOne
    @JoinColumn(name = "owner_PyroUserDB_id")
    public PyroUserDB owner;

    @ManyToOne
    @JoinColumn(name = "organization_PyroOrganizationDB_id")
    public PyroOrganizationDB organization;

    @Transient
    public boolean isLanguageEditor() {
        return this.image == null;
    }

    @Transient
    public boolean isModelEditor() {
        return !this.isLanguageEditor();
    }
}
