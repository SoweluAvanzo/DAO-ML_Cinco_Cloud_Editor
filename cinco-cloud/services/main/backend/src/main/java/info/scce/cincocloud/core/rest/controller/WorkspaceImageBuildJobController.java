package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.core.services.ProjectService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.core.services.WorkspaceImageBuildJobLogFileService;
import info.scce.cincocloud.core.services.WorkspaceImageBuildJobService;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
