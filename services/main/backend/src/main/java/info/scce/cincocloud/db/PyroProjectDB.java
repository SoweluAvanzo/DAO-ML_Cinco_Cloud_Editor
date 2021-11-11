package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
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

@Entity(name = "entity_core_pyroproject")
public class PyroProjectDB extends PanacheEntity {

  public String name;

  public String description;

  @NotNull
  @Enumerated(EnumType.STRING)
  public PyroProjectTypeDB type = PyroProjectTypeDB.LANGUAGE_EDITOR;

  /**
   * The image that has been generated from the project. If null, no image has been generated from the project yet.
   * {@see #type} should be PyroProjectTypeDB.LANGUAGE_EDITOR.
   */
  @OneToOne
  public PyroWorkspaceImageDB image;

  /**
   * The image that was used to create the project. If the property is null, it means we use the theia language editor.
   * If the property is not null, it means we use the pyro model editor. {@see #type} should be
   * PyroProjectTypeDB.MODEL_EDITOR.
   */
  @OneToOne
  public PyroWorkspaceImageDB template;

  @ManyToOne
  @JoinColumn(name = "owner_PyroUserDB_id")
  public PyroUserDB owner;

  @ManyToOne
  @JoinColumn(name = "organization_PyroOrganizationDB_id")
  public PyroOrganizationDB organization;

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project")
  public Collection<PyroWorkspaceImageBuildJobDB> buildJobs = new ArrayList<>();

  @Transient
  public boolean isLanguageEditor() {
    return this.type.equals(PyroProjectTypeDB.LANGUAGE_EDITOR);
  }

  @Transient
  public boolean isModelEditor() {
    return this.type.equals(PyroProjectTypeDB.MODEL_EDITOR);
  }
}
