package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class ProjectDB extends PanacheEntity {

  public String name;
  public String description;

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

  @Transient
  public boolean isLanguageEditor() {
    return this.type.equals(ProjectType.LANGUAGE_EDITOR);
  }

  @Transient
  public boolean isModelEditor() {
    return this.type.equals(ProjectType.MODEL_EDITOR);
  }
}
