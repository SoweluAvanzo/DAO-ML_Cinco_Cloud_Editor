package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.inputs.UpdateProjectInput;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.OrganizationAccessRight;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.ProjectType;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.sync.ProjectRegistry;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Transactional
public class ProjectService {

  private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());

  @Inject
  ProjectRegistry projectRegistry;

  @Inject
  FileService fileService;

  @Inject
  SettingsService settingsService;

  @Inject
  OrganizationAccessRightVectorService orgAccessRightVectorService;

  public ProjectDB getOrThrow(long projectId) {
    return (ProjectDB) ProjectDB.findByIdOptional(projectId)
        .orElseThrow(() -> new EntityNotFoundException("Cannot find project."));
  }

  public PanacheQuery<ProjectDB> getAllAccessibleProjects(UserDB subject) {
    return ProjectDB.findProjectsWhereUserIsOwnerOrMember(subject.id);
  }

  public ProjectDB createProject(
      String name,
      String description,
      UserDB subject,
      Optional<OrganizationDB> organizationOptional,
      Optional<WorkspaceImageDB> imageOptional) {
    final ProjectDB project = new ProjectDB();
    project.owner = organizationOptional.isPresent() ? null : subject;
    project.name = name;
    project.description = description;
    project.organization = organizationOptional.orElse(null);
    subject.personalProjects.add(project);
    organizationOptional.ifPresent(organization -> organization.projects.add(project));

    imageOptional.ifPresent(image -> {
      project.template = image;
      project.type = ProjectType.MODEL_EDITOR;
      copyLogoFromTemplate(project, image);
    });

    project.persist();
    subject.persist();
    organizationOptional.ifPresent(organization -> organization.persist());

    return project;
  }

  public void createDefaultProjects(UserDB user) {
    final var settings = settingsService.getSettings();
    if (settings.createDefaultProjects) {
      for (final var image: WorkspaceImageDB.findAllFeaturedImages().list()) {
        createProject(
          image.project.name,
          image.project.description,
          user,
          Optional.empty(),
          Optional.of(image)
        );
      }
    }
  }

  private void copyLogoFromTemplate(ProjectDB project, WorkspaceImageDB image) {
    if (image.project.logo != null) {
      try {
        final var filename = image.project.logo.filename;
        final var is = this.fileService.loadFile(image.project.logo);
        project.logo = this.fileService.storeFile(filename, is, image.project.logo.contentType);
      } catch (Exception e) {
        LOGGER.log(Level.INFO, "Could not copy project logo.", e);
      }
    }
  }

  public void deleteProject(ProjectDB project) {
    if (project.hasActiveBuildjobs()) {
      throw new IllegalArgumentException("Project has active buildjobs.");
    }

    if (project.owner != null) {
      project.owner.personalProjects.remove(project);
      // Set to null to prevent cascading deletion of the owner
      project.owner = null;
    }

    projectRegistry.closeSessions(project.id);

    // remove project from organization
    if (project.organization != null) {
      project.organization.projects.remove(project);
      project.organization.persist();

      // null references and mark project as deleted
      project.organization = null;
    }
    project.deletedAt = Instant.now();

    final var buildJobIds = project.buildJobs.stream()
        .map(j -> j.id)
        .collect(Collectors.toList());

    WorkspaceImageBuildJobDB.deleteByIdIn(buildJobIds);

    // remove featured image
    if (project.image != null) {
      project.image.featured = false;
      project.image.persist();
    }

    if (project.logo != null) {
      this.fileService.deleteFile(project.logo);
    }

    project.buildJobs.clear();
    project.persist();
  }

  public ProjectDB updateProject(UserDB user, Long projectId, UpdateProjectInput input) {
    final var project = getOrThrow(projectId);

    if (!userCanEditProject(user, project)) {
      throw new RestException(Response.Status.FORBIDDEN, "Insufficient access rights.");
    }

    project.name = input.name;
    project.description = input.description;

    boolean logoAdded = project.logo == null && input.logoId != null;
    boolean logoChanged = project.logo != null && input.logoId != null && !project.logo.id.equals(input.logoId);
    boolean logoRemoved = project.logo != null && input.logoId == null;

    if (logoChanged || logoAdded) {
      if (project.logo != null) {
        fileService.deleteFile(project.logo);
      }

      project.logo = (BaseFileDB) BaseFileDB.findByIdOptional(input.logoId)
              .orElseThrow(() -> new RestException(Response.Status.NOT_FOUND, "Logo file not found."));
    } else if (logoRemoved) {
      fileService.deleteFile(project.logo);
      project.logo = null;
    }

    return project;
  }

  public ProjectDB transferOwnershipToUser(UserDB targetUser, ProjectDB project) {
    // switch from organization to private project
    if (project.owner == null) {
      removeOrganizationFromProject(project);
      addPrivateOwnerToProject(project, targetUser);
      // switch owner in a private project (only update if the owner really changes)
    } else if (!project.owner.equals(targetUser)) {
      // add previous owner to the project member list, check for duplicates
      if (!project.members.contains(project.owner)) {
        project.members.add(project.owner);
      }
      removePrivateOwnerFromProject(project);
      // remove new owner from the project member list, if he was a member
      project.members.remove(targetUser);
      addPrivateOwnerToProject(project, targetUser);
    }
    return project;
  }

  public ProjectDB transferOwnershipToOrganization(OrganizationDB targetOrganization, ProjectDB project) {
    // switch from private project to organization
    if (project.organization == null) {
      removePrivateOwnerFromProject(project);
      addOrganizationToProject(project, targetOrganization);
      // switch from an organization to another organization
    } else if (!project.organization.equals(targetOrganization)) {
      removeOrganizationFromProject(project);
      addOrganizationToProject(project, targetOrganization);
    }
    return project;
  }

  public ProjectDB addMember(UserDB user, ProjectDB project) {
    if (project.owner != null && project.owner.equals(user)) {
      throw new IllegalArgumentException("The owner of the project cannot be added as a member.");
    }
    if (project.members.contains(user)) {
      throw new IllegalArgumentException("The user is already a member of the project.");
    }

    project.members.add(user);
    project.persist();

    return project;
  }

  public ProjectDB removeMember(UserDB user, ProjectDB project) {
    if (!project.members.remove(user)) {
      throw new IllegalArgumentException("The user is not a member of the project.");
    }

    project.persist();

    return project;
  }

  public boolean userHasMemberStatus(UserDB user, ProjectDB project) {
    return project.matchOnMembership(
        members -> members.contains(user),
        organization -> organization.members.contains(user)
    );
  }

  public boolean userHasOwnerStatus(UserDB user, ProjectDB project) {
    return project.matchOnOwnership(
        owner -> user.equals(owner),
        organization -> organization.owners.contains(user)
    );
  }

  public boolean userCanAccessProject(UserDB user, ProjectDB project) {
    return userHasOwnerStatus(user, project) || userHasMemberStatus(user, project);
  }

  public boolean userCanCreateProject(UserDB user, Optional<OrganizationDB> organizationOptional) {
    if (organizationOptional.isPresent()) {
      final OrganizationDB org = organizationOptional.get();
      OrganizationAccessRightVectorDB arv = orgAccessRightVectorService.getByUserAndOrganization(user, org);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.CREATE_PROJECTS);
    } else {
      return true;
    }
  }

  public boolean userCanEditProject(UserDB user, ProjectDB project) {
    if (user.isAdmin()) return true;
    if (project.organization == null) {
      return project.owner.equals(user);
    } else {
      OrganizationAccessRightVectorDB arv = orgAccessRightVectorService.getByUserAndOrganization(user, project.organization);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.EDIT_PROJECTS);
    }
  }

  public boolean userCanDeleteProject(UserDB user, ProjectDB project) {
    if (user.isAdmin()) return true;
    if (project.organization == null) {
      return project.owner.equals(user);
    } else {
      OrganizationAccessRightVectorDB arv = orgAccessRightVectorService.getByUserAndOrganization(user, project.organization);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.DELETE_PROJECTS);
    }
  }

  private void addOrganizationToProject(ProjectDB project, OrganizationDB org) {
    org.projects.add(project);
    org.persist();
    project.organization = org;
    project.persist();
    addAllOrganizationMembersToProjectMembers(project);
    removeAllNonOrganizationMembersFromProject(project);
  }

  private void removeOrganizationFromProject(ProjectDB project) {
    removeOrganizationFromProject(project, project.organization);
  }

  private void removeOrganizationFromProject(ProjectDB project, OrganizationDB org) {
    if (project.organization.equals(org) && org.projects.contains(project)) {
      removeAllOrganizationMembersFromProjectMembers(project);
      org.projects.remove(project);
      org.persist();
      project.organization = null;
      project.persist();
    } else {
      throw new IllegalArgumentException("Organization is not part of this project and vice versa");
    }
  }

  private void addPrivateOwnerToProject(ProjectDB project, UserDB owner) {
    project.owner = owner;
    project.persist();
    if (!owner.personalProjects.contains(project)) {
      owner.personalProjects.add(project);
    }
    owner.persist();
  }

  private void removePrivateOwnerFromProject(ProjectDB project) {
    removePrivateOwnerFromProject(project, project.owner);
  }

  private void removePrivateOwnerFromProject(ProjectDB project, UserDB owner) {
    if (project.owner.equals(owner) && owner.personalProjects.contains(project)) {
      owner.personalProjects.remove(project);
      owner.persist();
      project.owner = null;
      project.persist();
    } else {
      throw new IllegalArgumentException("User is not part owner of this project and vice versa");
    }
  }

  private void addAllOrganizationMembersToProjectMembers(ProjectDB project) {
    if (project.organization != null) {
      Stream.concat(project.organization.members.stream(), project.organization.owners.stream())
          .filter(u -> !project.members.contains(u))
          .forEach(u -> project.members.add(u));
      project.persist();
    }
  }

  private void removeAllOrganizationMembersFromProjectMembers(ProjectDB project) {
    if (project.organization != null) {
      Stream.concat(project.organization.members.stream(), project.organization.owners.stream())
          .forEach(u -> project.members.remove(u));
      project.persist();
    }
  }

  private void removeAllNonOrganizationMembersFromProject(ProjectDB project) {
    if (project.organization != null) {
      final var usersToRemove = project.members.stream()
          .filter(m -> !project.organization.members.contains(m) && !project.organization.owners.contains(m))
          .collect(Collectors.toList());

      project.members.removeAll(usersToRemove);
      project.persist();
    }
  }
}

