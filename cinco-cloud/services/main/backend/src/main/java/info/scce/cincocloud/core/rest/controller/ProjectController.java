package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UpdateProjectInput;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectTransferToOrganizationInput;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectTransferToUserInput;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectUsersInput;
import info.scce.cincocloud.core.rest.tos.BooleanTO;
import info.scce.cincocloud.core.rest.tos.GitInformationTO;
import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.core.services.OrganizationService;
import info.scce.cincocloud.core.services.ProjectDeploymentService;
import info.scce.cincocloud.core.services.ProjectService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.core.services.WorkspaceImageService;
import info.scce.cincocloud.db.GitInformationDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.proto.CincoCloudProtos;
import info.scce.cincocloud.rest.ObjectCache;

import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
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
@Path("/projects")
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
  WorkspaceImageService workspaceImageService;

  @Inject 
  ObjectCache objectCache;

  @POST
  @RolesAllowed("user")
  public Response createProject(@Context SecurityContext securityContext, ProjectTO newProject) {
    final var subject = UserService.getCurrentUser(securityContext);

    if (newProject.getorganization() != null) {
      throw new RestException(Status.BAD_REQUEST, "Project contains an organization.");
    }

    final Optional<WorkspaceImageDB> imageOptional = Optional
        .ofNullable(newProject.getTemplate())
        .map(i -> workspaceImageService.getOrThrow(i.getId()));

    if (imageOptional.isPresent()) {
      final var image = imageOptional.get();

      // check if the user is not using an image from an organization for personal use
      final var org = image.project.organization;
      if (!image.published && newProject.getorganization() == null && org != null) {
        throw new RestException(Response.Status.BAD_REQUEST, "You cannot use the image for personal projects.");
      }

      if (!image.published && !projectService.userHasOwnerStatus(subject, image.project)) {
        throw new RestException(Response.Status.BAD_REQUEST, "You are not allowed to use this image.");
      }
    }

    final ProjectDB project = projectService.createProject(
        newProject.getname(),
        newProject.getdescription(),
        subject,
        Optional.empty(),
        imageOptional
    );

    return Response.status(Status.CREATED).entity(ProjectTO.fromEntity(project, objectCache)).build();
  }

  @PUT
  @Path("/{projectId}/rpc/transfer-to-user")
  @RolesAllowed("user")
  public Response transferOwnershipToUser(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @Valid UpdateProjectTransferToUserInput input
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);
    final UserDB targetUser = userService.getOrThrow(input.getUserId());

    if (!projectService.userCanEditProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var updatedProject = projectService.transferOwnershipToUser(targetUser, project);

    return Response.ok(ProjectTO.fromEntity(updatedProject, objectCache)).build();
  }

  @PUT
  @Path("/{projectId}/rpc/transfer-to-organization")
  @RolesAllowed("user")
  public Response transferOwnershipToOrganization(
      @Context final SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @Valid UpdateProjectTransferToOrganizationInput input
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);
    final OrganizationDB targetOrganization = organizationService.getOrThrow(input.getOrgId());

    if (!projectService.userCanEditProject(subject, project) ||
        !organizationService.userCanAccessOrganization(subject, targetOrganization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var updatedProject = projectService.transferOwnershipToOrganization(targetOrganization, project);

    return Response.ok(ProjectTO.fromEntity(updatedProject, objectCache)).build();
  }

  @PUT
  @Path("/{projectId}")
  @RolesAllowed("user")
  public Response updateProject(
          @Context SecurityContext securityContext,
          @PathParam("projectId") final long projectId,
          @Valid UpdateProjectInput input
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final var updatedProject = projectService.updateProject(subject, projectId, input);
    return Response.ok(ProjectTO.fromEntity(updatedProject, objectCache)).build();
  }

  @GET
  @Path("/{projectId}")
  @RolesAllowed("user")
  public Response getProject(
      @Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
  }

  @GET
  @RolesAllowed("user")
  public Response getProjects(
          @Context SecurityContext securityContext,
          @QueryParam("page") @DefaultValue("0") Integer index,
          @QueryParam("size") @DefaultValue("25") Integer size
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final var query = projectService.getAllAccessibleProjects(subject);
    final var pageTO = PageTO.ofQuery(query, index, size, p -> ProjectTO.fromEntity(p, objectCache));
    return Response.ok(pageTO).build();
  }

  @PUT
  @Path("/{projectId}/git-information")
  @RolesAllowed("user")
  public Response updateGitInformation(
      @Context SecurityContext securityContext,
      GitInformationTO gitInformationTO,
      @PathParam("projectId") final long projectId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanEditProject(subject, project)) {
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
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanEditProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    return Response.ok(getOrBuildGitInformationTO(project)).build();
  }

  @POST
  @Path("/{projectId}/members")
  @RolesAllowed("user")
  public Response addMember(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @Valid UpdateProjectUsersInput input) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var updatedProject = projectService.addMember(userService.getOrThrow(input.getUserId()), project);

    return Response.ok(ProjectTO.fromEntity(updatedProject, objectCache)).build();
  }

  @DELETE
  @Path("/{projectId}/members/{userId}")
  @RolesAllowed("user")
  public Response removeMember(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @PathParam("userId") final long userId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var updatedProject = projectService.removeMember(userService.getOrThrow(userId), project);

    return Response.ok(ProjectTO.fromEntity(updatedProject, objectCache)).build();
  }

  @POST
  @Path("/{projectId}/deployments")
  @RolesAllowed("user")
  public Response deployProject(@Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @QueryParam("redeploy") @DefaultValue("false") final boolean redeploy) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var result = redeploy
        ? projectDeploymentService.redeploy(project)
        : projectDeploymentService.deploy(project);

    return Response.ok(result).build();
  }

  @DELETE
  @Path("/{projectId}/deployments")
  @RolesAllowed("user")
  public Response stopDeployedProject(
      @Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    projectDeploymentService.stop(project);

    return Response.status(Response.Status.OK).build();
  }

  @DELETE
  @Path("/{projectId}")
  @RolesAllowed("user")
  public Response removeProject(
      @Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanDeleteProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    projectDeploymentService.delete(project);
    projectService.deleteProject(project);

    return Response.status(Status.NO_CONTENT).build();
  }

  @GET
  @Path("/{projectId}/rpc/has-active-build-jobs")
  @RolesAllowed("user")
  public Response hasActiveBuildJobs(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var hasActiveBuildJobs = project.buildJobs.stream().anyMatch(job -> !job.isTerminated());

    return Response.ok(new BooleanTO(hasActiveBuildJobs)).build();
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
