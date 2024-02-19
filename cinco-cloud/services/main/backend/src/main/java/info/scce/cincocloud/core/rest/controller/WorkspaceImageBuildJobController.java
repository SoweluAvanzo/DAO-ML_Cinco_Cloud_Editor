package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.core.services.ProjectService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.core.services.WorkspaceImageBuildJobLogFileService;
import info.scce.cincocloud.core.services.WorkspaceImageBuildJobService;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import jakarta.annotation.security.RolesAllowed;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/projects/{projectId}/build-jobs")
@RequestScoped
public class WorkspaceImageBuildJobController {

  @Inject
  ObjectCache objectCache;

  @Inject
  ProjectService projectService;

  @Inject
  WorkspaceImageBuildJobService workspaceImageBuildJobService;

  @Inject
  WorkspaceImageBuildJobLogFileService logFileService;

  @GET
  @RolesAllowed("user")
  public Response getAll(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId,
      @QueryParam("page") @DefaultValue("0") final int page,
      @QueryParam("size") @DefaultValue("25") final int size
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    return Response.ok(workspaceImageBuildJobService.getAllPaged(project, page, size)).build();
  }

  @GET
  @Path("/{jobId}")
  @RolesAllowed("user")
  public Response get(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @PathParam("jobId") final Long jobId
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var job = workspaceImageBuildJobService.getOrThrow(jobId);

    return Response.ok(WorkspaceImageBuildJobTO.fromEntity(job, objectCache)).build();
  }

  @DELETE
  @Path("/{jobId}")
  @RolesAllowed("user")
  public Response delete(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @PathParam("jobId") final Long jobId
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanDeleteProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    workspaceImageBuildJobService.delete(jobId);

    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("/{jobId}/rpc/abort")
  @RolesAllowed("user")
  public Response abort(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @PathParam("jobId") final Long jobId
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userHasOwnerStatus(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var job = workspaceImageBuildJobService.abort(jobId);

    return Response.ok(WorkspaceImageBuildJobTO.fromEntity(job, objectCache)).build();
  }

  @GET
  @Path("/{jobId}/log")
  @RolesAllowed("user")
  public Response getBuildLog(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @PathParam("jobId") final Long jobId
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final ProjectDB project = projectService.getOrThrow(projectId);

    if (!projectService.userCanAccessProject(subject, project)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    var log = logFileService.getBuildLog(projectId, jobId)
        .orElseThrow(() -> new RestException(Status.NOT_FOUND, "Could not find build log."));

    return Response.ok(log).build();
  }
}
