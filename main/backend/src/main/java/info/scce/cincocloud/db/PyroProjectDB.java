package info.scce.cincocloud.db;

import javax.persistence.Entity;

@Entity(name = "entity_core_pyroproject")
public class PyroProjectDB extends PyroFileContainerDB {

    public String name;

    public String description;

    @javax.persistence.ManyToOne
    @javax.persistence.JoinColumn(name = "owner_PyroUserDB_id")
    public PyroUserDB owner;

    @javax.persistence.ManyToOne
    @javax.persistence.JoinColumn(name = "organization_PyroOrganizationDB_id")
    public PyroOrganizationDB organization;
}
