package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.BooleanTO;
import info.scce.cincocloud.core.rest.tos.GitInformationTO;
import info.scce.cincocloud.core.rest.tos.OrganizationTO;
import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.*;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/project")
@RequestScoped
public class ProjectController {

  @Inject 
  ProjectService projectService;

  @Inject
  OrganizationService organizationService;

  @Inject
  UserService userService;

  @Inject 
  ProjectDeploymentService projectDeploymentService;

  @Inject 
  ObjectCache objectCache;

  @POST
  @Path("/create/private")
  @RolesAllowed("user")
  public Response createProject(@Context SecurityContext securityContext, ProjectTO newProject) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    Optional<OrganizationDB> organizationOptional;
    if (newProject.getorganization() == null) {
      organizationOptional = Optional.empty();
    } else {
      final OrganizationDB org = OrganizationDB.findById(newProject.getorganization().getId());
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      organizationOptional = Optional.of(org);
    }

    final Optional<WorkspaceImageDB> imageOptional = Optional
        .ofNullable(newProject.getTemplate())
        .map(i -> WorkspaceImageDB.findById(i.getId()));

    if (imageOptional.isPresent()) {
      final WorkspaceImageDB image = imageOptional.get();
      if (!image.published && !projectService.userOwnsProject(subject, image.project)) {
        throw new RestException(Response.Status.BAD_REQUEST, "You are not allowed to use this image.");
      }
    }

    if (canCreateProject(subject, organizationOptional)) {
      final ProjectDB project = createProject(
          newProject.getname(),
          newProject.getdescription(),
          subject,
          organizationOptional,
          imageOptional
      );

      return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
    }
    throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
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
    });

    project.persist();
    subject.persist();
    organizationOptional.ifPresent(organization -> organization.persist());

    return project;
  }

  @PUT
  @Path("/{projectId}/owner/private")
  @RolesAllowed("user")
  public Response transferOwnershipToUser(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      final UserTO targetUser
  ) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    if (canEditProject(subject, project)) {
      final UserDB targetUserDB = UserDB.findById(targetUser.getId());
      userService.checkIfUserExists(targetUserDB);
      // switch from organization to private project
      if (project.owner == null) {
        removeOrganizationFromProject(project);
        addPrivateOwnerToProject(project, targetUserDB);
      // switch owner in a private project (only update if the owner really changes)
      } else if (!project.owner.equals(targetUserDB)) {
        // add previous owner to the project member list, check for duplicates
        if (!project.members.contains(project.owner)) {
          project.members.add(project.owner);
        }
        removePrivateOwnerFromProject(project);
        // remove new owner from the project member list, if he was a member
        if (project.members.contains(targetUserDB)){
          project.members.remove(targetUserDB);
        }
        addPrivateOwnerToProject(project, targetUserDB);
      }
      return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
    }

    throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
  }

  @PUT
  @Path("/{projectId}/organization/private")
  @RolesAllowed("user")
  public Response transferOwnershipToOrganization(
      @Context final SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      final OrganizationTO targetOrganization
  ) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    if (canEditProject(subject, project)) {
      final OrganizationDB targetOrganizationDB = OrganizationDB.findById(targetOrganization.getId());
      organizationService.checkIfOrganizationExists(targetOrganizationDB);
      organizationService.checkIfUserIsOrganizationMemberOrOwner(targetOrganizationDB, subject);

      // switch from private project to organization
      if (project.organization == null) {
        removePrivateOwnerFromProject(project);
        addOrganizationToProject(project, targetOrganizationDB);
      // switch from an organization to another organization
      } else if (!project.organization.equals(targetOrganizationDB)) {
        removeOrganizationFromProject(project);
        addOrganizationToProject(project, targetOrganizationDB);
      }
      return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
    }

    throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
  }

  @POST
  @Path("/update/private")
  @RolesAllowed("user")
  public Response updateProject(
      @Context SecurityContext securityContext, ProjectTO updatedProject) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(updatedProject.getId());
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    if (canEditProject(subject, project)) {
      project.description = updatedProject.getdescription();
      project.name = updatedProject.getname();

      if (updatedProject.getLogo() != null) {
        project.logo = BaseFileDB.findById(updatedProject.getLogo().getId());
      } else {
        project.logo = null;
      }

      project.persist();
      return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
    }
    throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
  }

  @GET
  @Path("/{projectId}")
  @RolesAllowed("user")
  public Response getProject(
      @Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);
    return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
  }

  @GET
  @Path("/private")
  @RolesAllowed("user")
  public Response getProjects(@Context SecurityContext securityContext) {
    final var subject = UserDB.getCurrentUser(securityContext);

    final var projects = ProjectDB.findProjectsWhereUserIsOwnerOrMember(subject.id).stream()
        .map(p -> ProjectTO.fromEntity(p, objectCache))
        .collect(Collectors.toList());

    return Response.ok(projects).build();
  }

  @POST
  @Path("/{projectId}/git-information")
  @RolesAllowed("user")
  public Response updateGitInformation(
      @Context SecurityContext securityContext,
      GitInformationTO gitInformationTO,
      @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);

    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    if (!canEditProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    mergeGitInformation(gitInformationTO, project);
    project.persistAndFlush();

    return Response.ok(getOrBuildGitInformationTO(project)).build();
  }

  @GET
  @Path("/{projectId}/git-information")
  @RolesAllowed("user")
  public Response getGitInformation(
      @Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);

    if (!canEditProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    return Response.ok(getOrBuildGitInformationTO(project)).build();
  }

  @POST
  @Path("/{projectId}/member/private")
  @RolesAllowed("user")
  public Response addMember(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      UserTO user) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    final UserDB userToAdd = UserDB.findById(user.getId());
    if (userToAdd == null) {
      throw new RestException(Status.NOT_FOUND, "the user could not be found");
    } else if (project.members.contains(userToAdd)) {
      throw new RestException(Status.BAD_REQUEST, "the user is already a member of the project");
    } else if (project.owner != null && project.owner.equals(userToAdd)) {
      throw new RestException(Status.BAD_REQUEST, "the owner of the project cannot be added as member");
    }

    project.members.add(userToAdd);
    project.persist();

    return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
  }

  @DELETE
  @Path("/{projectId}/member/{userId}/private")
  @RolesAllowed("user")
  public Response removeMember(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @PathParam("userId") final long userId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    final UserDB userToRemove = UserDB.findById(userId);
    if (userToRemove == null) {
      throw new RestException(Status.NOT_FOUND, "the user could not be found");
    } else if (!project.members.contains(userToRemove)) {
      throw new RestException(Status.BAD_REQUEST, "the user is not a member of the project");
    }

    project.members.remove(userToRemove);
    project.persist();

    return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
  }

  @POST
  @Path("/{projectId}/deployments/private")
  @RolesAllowed("user")
  public Response deployProject(@Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @QueryParam("redeploy") @DefaultValue("false") final boolean redeploy) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);
    final var result = redeploy
        ? projectDeploymentService.redeploy(project)
        : projectDeploymentService.deploy(project);
    return Response.ok(result).build();
  }

  @DELETE
  @Path("/{projectId}/deployments/private")
  @RolesAllowed("user")
  public Response stopDeployedProject(
      @Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);
    projectDeploymentService.stop(project);
    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("/remove/{id}/private")
  @RolesAllowed("user")
  public Response removeProject(
      @Context SecurityContext securityContext, @PathParam("id") final long id) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(id);
    projectService.checkIfProjectExists(project);
    if (canDeleteProject(subject, project)) {
      if (project.hasActiveBuildjobs()) throw new RestException(Status.BAD_REQUEST, "Project has active buildjobs.");
      projectService.deleteById(id);
      projectDeploymentService.delete(project);
      return Response.status(Status.NO_CONTENT).build();
    }
    throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
  }

  @GET
  @Path("/{projectId}/rpc/has-active-build-jobs/private")
  @RolesAllowed("user")
  public Response hasActiveBuildJobs(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId
  ) {
    final var subject = UserDB.getCurrentUser(securityContext);
    final var project = (ProjectDB) ProjectDB
        .findByIdOptional(projectId)
        .orElseThrow(() -> new EntityNotFoundException("The project (id: " + projectId + ") could not be found."));

    projectService.checkPermission(project, subject);

    final var hasActiveBuildJobs = project.buildJobs.stream().anyMatch(job -> !job.isTerminated());

    return Response.ok(new BooleanTO(hasActiveBuildJobs)).build();
  }

  private boolean canCreateProject(UserDB user, Optional<OrganizationDB> organizationOptional) {
    if (organizationOptional.isPresent()) {
      final OrganizationDB org = organizationOptional.get();
      OrganizationAccessRightVectorDB arv = getAccessRightVector(user, org);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.CREATE_PROJECTS);
    } else {
      return true;
    }
  }

  private boolean canEditProject(UserDB user, ProjectDB project) {
    if (project.organization == null) {
      return project.owner.equals(user);
    } else {
      OrganizationAccessRightVectorDB arv = getAccessRightVector(user, project.organization);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.EDIT_PROJECTS);
    }
  }

  private boolean canDeleteProject(UserDB user, ProjectDB project) {
    if (project.organization == null) {
      return project.owner.equals(user);
    } else {
      OrganizationAccessRightVectorDB arv = getAccessRightVector(user, project.organization);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.DELETE_PROJECTS);
    }
  }

  private OrganizationAccessRightVectorDB getAccessRightVector(UserDB user, OrganizationDB org) {
    final List<OrganizationAccessRightVectorDB> result =
        OrganizationAccessRightVectorDB.findOrganizationAccessRightsForUser(user, org)
            .stream()
            .collect(Collectors.toList());
    return result.size() == 1 ? result.get(0) : null;
  }

  private void mergeGitInformation(GitInformationTO gitInformationTO, ProjectDB project) {
    if (gitInformationTO.getType().equals(CincoCloudProtos.GetGitInformationReply.Type.NONE)) {
      project.gitInformation = null;
    } else {
      if (project.gitInformation == null) project.gitInformation = new GitInformationDB();
      project.gitInformation.type = gitInformationTO.getType();
      project.gitInformation.password = gitInformationTO.getPassword();
      project.gitInformation.repositoryUrl = gitInformationTO.getRepositoryUrl();
      project.gitInformation.branch = gitInformationTO.getBranch();
      project.gitInformation.username = gitInformationTO.getUsername();
      project.gitInformation.genSubdirectory = gitInformationTO.getGenSubdirectory();
      project.gitInformation.project = project;
    }
  }

  private GitInformationTO getOrBuildGitInformationTO(ProjectDB project) {
    if (project.gitInformation != null) return GitInformationTO.fromEntity(project.gitInformation);

    final var r = new GitInformationTO();
    r.setProjectId(project.id);
    r.setType(CincoCloudProtos.GetGitInformationReply.Type.NONE);

    return r;
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
