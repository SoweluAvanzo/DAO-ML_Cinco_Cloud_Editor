package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(
    name = "ProjectDB.findWhereUserIsOwnerOrMember",
    query = "select distinct project "
        + "from ProjectDB project, UserDB user "
        + "where project.deletedAt is null and ("
        + "(project.owner is not null and project.owner.id = ?1) or "
        + "(user.id = ?1 and user in elements(project.members)))"
)
public class ProjectDB extends PanacheEntity {

  public String name;
  public String description;

  @OneToOne(cascade = CascadeType.ALL)
  public BaseFileDB logo;

  /**
   * If != null, the project is considered deleted.
   */
  public Instant deletedAt;

  @NotNull
  @Enumerated(EnumType.STRING)
  public ProjectType type = ProjectType.LANGUAGE_EDITOR;

  /**
   * The image that has been generated from the project. If null, no image has been generated from the project yet.
   * {@see #type} should be ProjectType.LANGUAGE_EDITOR.
   */
  @OneToOne
  public WorkspaceImageDB image;

  /**
   * The image that was used to create the project. If the property is null, it means we use the theia language editor.
   * If the property is not null, it means we use the pyro model editor. {@see #type} should be
   * ProjectType.MODEL_EDITOR.
   */
  @OneToOne
  public WorkspaceImageDB template;

  @ManyToOne
  @JoinColumn(name = "owner_UserDB_id")
  public UserDB owner;

  @ManyToOne
  @JoinColumn(name = "organization_OrganizationDB_id")
  public OrganizationDB organization;

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
  public Collection<WorkspaceImageBuildJobDB> buildJobs = new ArrayList<>();

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
  public Collection<GraphModelTypeDB> graphModelTypes = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  public GitInformationDB gitInformation;

  @Transient
  public boolean isLanguageEditor() {
    return this.type.equals(ProjectType.LANGUAGE_EDITOR);
  }

  @Transient
  public boolean isModelEditor() {
    return this.type.equals(ProjectType.MODEL_EDITOR);
  }

  @Transient
  public boolean hasActiveBuildjobs() {
    return this.buildJobs.stream().anyMatch(job -> !job.isTerminated());
  }

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "ProjectDB_members",
      joinColumns = @JoinColumn(name = "ProjectDB_id"),
      inverseJoinColumns = @JoinColumn(name = "UserDB_id")
  )
  public Collection<UserDB> members = new HashSet<>();

  public static PanacheQuery<ProjectDB> findProjectsWhereUserIsOwnerOrMember(long userId) {
    return find("#ProjectDB.findWhereUserIsOwnerOrMember", userId);
  }

  public <T> T matchOnOwnership(
      Function<UserDB, T> personalCase,
      Function<OrganizationDB, T> organizationCase
  ) {
    if (owner != null && organization == null) {
      return personalCase.apply(owner);
    } else if (owner == null && organization != null) {
      return organizationCase.apply(organization);
    } else if (owner == null && organization == null) {
      throw new RuntimeException(String.format("Project %d is neither owned by a user nor by an organization", id));
    } else if (owner != null && organization != null) {
      throw new RuntimeException(String.format("Project %d is both owned by a user and by an organization", id));
    } else {
      throw new RuntimeException("Missing case in ProjectDB::matchOnOwnership");
    }
  }

  public <T> T matchOnMembership(
      Function<Collection<UserDB>, T> personalCase,
      Function<OrganizationDB, T> organizationCase
  ) {
    if (owner != null && organization == null) {
      return personalCase.apply(members);
    } else if (owner == null && organization != null) {
      return organizationCase.apply(organization);
    } else if (owner == null && organization == null) {
      throw new RuntimeException(String.format("Project %d is neither owned by a user nor by an organization", id));
    } else if (owner != null && organization != null) {
      throw new RuntimeException(String.format("Project %d is both owned by a user and by an organization", id));
    } else {
      throw new RuntimeException("Missing case in ProjectDB::matchOnMembership");
    }
  }
}
