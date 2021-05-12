package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity()
public class PyroWorkspaceImageDB extends PanacheEntity {

    @NotBlank
    public String name;

    @NotBlank
    public String imageName;

    @NotBlank
    public String imageVersion;

    @NotNull
    public boolean published = false;

    @NotNull
    @ManyToOne
    public PyroUserDB user;
}
