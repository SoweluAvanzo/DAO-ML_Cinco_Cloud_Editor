package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.GitInformationTO;
import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.*;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
      final OrganizationDB org = OrganizationDB
          .findById(newProject.getorganization().getId());
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
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  public ProjectDB createProject(
      String name,
      String description,
      UserDB subject,
      Optional<OrganizationDB> organizationOptional,
      Optional<WorkspaceImageDB> imageOptional
  ) {
    final ProjectDB project = new ProjectDB();
    project.owner = organizationOptional.isPresent() ? null : subject;
    project.name = name;
    project.description = description;
    project.organization = organizationOptional.orElse(null);
    subject.ownedProjects.add(project);
    organizationOptional
        .ifPresent(organization -> organization.projects.add(project));

    imageOptional.ifPresent(image -> {
      project.template = image;
      project.type = ProjectType.MODEL_EDITOR;
    });

    project.persist();
    subject.persist();
    organizationOptional.ifPresent(organization -> organization.persist());

    return project;
  }

  @POST
  @Path("/update/private")
  @RolesAllowed("user")
  public Response updateProject(@Context SecurityContext securityContext,
      ProjectTO ownedProject) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(ownedProject.getId());
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    if (canEditProject(subject, project)) {
      project.description = ownedProject.getdescription();
      project.name = ownedProject.getname();

      if (ownedProject.getLogo() != null) {
        project.logo = BaseFileDB.findById(ownedProject.getLogo().getId());
      } else {
        project.logo = null;
      }

      project.persist();
      return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @GET
  @Path("/{projectId}")
  @RolesAllowed("user")
  public Response getProject(@Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId) {
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
  public Response updateGitInformation(@Context SecurityContext securityContext,
                                       GitInformationTO gitInformationTO,
                                       @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);

    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);

    if (!canEditProject(subject, project)) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    mergeGitInformation(gitInformationTO, project);
    project.persistAndFlush();

    return Response.ok(getOrBuildGitInformationTO(project)).build();
  }

  @GET
  @Path("/{projectId}/git-information")
  @RolesAllowed("user")
  public Response getGitInformation(@Context SecurityContext securityContext,
                                    @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB ownedProject = ProjectDB.findById(projectId);

    if (!canEditProject(subject, ownedProject)) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    return Response.ok(getOrBuildGitInformationTO(ownedProject)).build();
  }

  @POST
  @Path("/{projectId}/member/private")
  @RolesAllowed("user")
  public Response addMember(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      UserTO user
  ) {
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
      @PathParam("userId") final long userId
  ) {
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
      @PathParam("projectId") final long projectId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkIfProjectExists(project);
    projectService.checkPermission(project, subject);
    final var result = projectDeploymentService.deploy(project);
    return Response.ok(result).build();
  }

  @DELETE
  @Path("/{projectId}/deployments/private")
  @RolesAllowed("user")
  public Response stopDeployedProject(@Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId) {
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
  public Response removeProject(@Context SecurityContext securityContext,
      @PathParam("id") final long id) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);
    final ProjectDB project = ProjectDB.findById(id);
    projectService.checkIfProjectExists(project);
    if (canDeleteProject(subject, project)) {
      projectService.deleteById(id);
      projectDeploymentService.delete(project);
      return Response.ok("Removed").build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean canCreateProject(
      UserDB user,
      Optional<OrganizationDB> organizationOptional) {
    if (organizationOptional.isPresent()) {
      final OrganizationDB org = organizationOptional.get();
      OrganizationAccessRightVectorDB arv = getAccessRightVector(user, org);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.CREATE_PROJECTS);
    } else {
      return true;
    }
  }

  private boolean canEditProject(
      UserDB user,
      ProjectDB project) {
    if (project.organization == null) {
      return project.owner.equals(user);
    } else {
      OrganizationAccessRightVectorDB arv = getAccessRightVector(user, project.organization);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.EDIT_PROJECTS);
    }
  }

  private boolean canDeleteProject(
      UserDB user,
      ProjectDB project) {
    if (project.organization == null) {
      return project.owner.equals(user);
    } else {
      OrganizationAccessRightVectorDB arv = getAccessRightVector(user, project.organization);
      return arv != null && arv.accessRights.contains(OrganizationAccessRight.DELETE_PROJECTS);
    }
  }

  private OrganizationAccessRightVectorDB getAccessRightVector(
      UserDB user,
      OrganizationDB org
  ) {
    final List<OrganizationAccessRightVectorDB> result = OrganizationAccessRightVectorDB
        .list("user = ?1 and organization = ?2", user, org);
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
}
