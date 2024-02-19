package info.scce.cincocloud.db;

import info.scce.cincocloud.proto.CincoCloudProtos;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;

@Entity
@NamedQuery(
        name = "GitInformationDB.findByProjectId",
        query = "select gitInfo from GitInformationDB gitInfo inner join gitInfo.project p where p.id = ?1"
)
public class GitInformationDB extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    public CincoCloudProtos.GetGitInformationReply.Type type;

    public String repositoryUrl;

    public String username;

    public String password;

    public String branch;

    public String genSubdirectory;

    @OneToOne
    public ProjectDB project;

    public static PanacheQuery<GitInformationDB> findByProjectId(Long projectId) {
        return find("#GitInformationDB.findByProjectId", projectId);
    }

}
