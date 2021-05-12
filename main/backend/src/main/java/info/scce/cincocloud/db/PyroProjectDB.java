package info.scce.cincocloud.db;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "entity_core_pyroproject")
public class PyroProjectDB extends PyroFileContainerDB {

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
}
