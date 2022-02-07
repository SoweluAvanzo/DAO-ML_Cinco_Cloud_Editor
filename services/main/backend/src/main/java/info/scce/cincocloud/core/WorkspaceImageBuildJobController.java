package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.mq.WorkspaceImageAbortBuildJobMessage;
import info.scce.cincocloud.mq.WorkspaceMQProducer;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.panache.common.Page;
import java.util.stream.Collectors;
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
@Path("/projects/{projectId}")
@RequestScoped
public class WorkspaceImageBuildJobController {

  @Inject
  ObjectCache objectCache;

  @Inject
  WorkspaceMQProducer workspaceMQProducer;

  @GET
  @Path("/build-jobs/private")
  @RolesAllowed("user")
  public Response getAll(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @QueryParam("page") @DefaultValue("0") final int page,
      @QueryParam("size") @DefaultValue("25") final int size
  ) {
    final var subject = UserDB.getCurrentUser(securityContext);
    final var project = (ProjectDB) ProjectDB
        .findByIdOptional(projectId)
        .orElseThrow(() -> new EntityNotFoundException("The project (id: " + projectId + ") could not be found."));

    if (!isMemberOfOrganization(subject, project.organization)) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    final var query = WorkspaceImageBuildJobDB
        .findByProjectIdOrderByStartedAtDesc(projectId);

    final var amountOfPages = (long) Math.ceil((double) query.count() / size);

    final var items = query
        .page(Page.of(page, size)).stream()
        .map(j -> WorkspaceImageBuildJobTO.fromEntity(j, objectCache))
        .collect(Collectors.toList());

    final var currentPage = new PageTO<WorkspaceImageBuildJobTO>(items, page, size, amountOfPages);

    return Response.ok(currentPage).build();
  }

  @DELETE
  @Path("/build-jobs/{jobId}/private")
  @RolesAllowed("user")
  public Response delete(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @PathParam("jobId") final Long jobId
  ) {
    final var subject = UserDB.getCurrentUser(securityContext);
    final var job = getJobById(jobId);
    checkUserIsProjectOwner(subject, job.project, "You are not allowed to delete the job");

    if (!job.isTerminated()) {
      throw new RestException(Status.BAD_REQUEST, "The job (id: " + job.id + ") is still running");
    }

    job.delete();

    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @POST
  @Path("/build-jobs/{jobId}/abort/private")
  @RolesAllowed("user")
  public Response abort(
      @Context SecurityContext securityContext,
      @PathParam("projectId") final Long projectId,
      @PathParam("jobId") final Long jobId
  ) {
    final var subject = UserDB.getCurrentUser(securityContext);
    final var job = getJobById(jobId);
    checkUserIsProjectOwner(subject, job.project, "You are not allowed to abort the job");

    if (job.isTerminated()) {
      throw new RestException(Status.BAD_REQUEST, "The job (id: " + job.id + ") has already been terminated");
    }

    job.status = WorkspaceImageBuildJobDB.Status.ABORTED;
    job.persist();

    workspaceMQProducer.send(new WorkspaceImageAbortBuildJobMessage(job.id));

    return Response.ok(WorkspaceImageBuildJobTO.fromEntity(job, objectCache)).build();
  }

  private void checkUserIsProjectOwner(UserDB user, ProjectDB project, String message) {
    if (!project.owner.equals(user)) {
      throw new RestException(Status.FORBIDDEN, message);
    }
  }

  private WorkspaceImageBuildJobDB getJobById(final Long jobId) {
    return (WorkspaceImageBuildJobDB) WorkspaceImageBuildJobDB
        .findByIdOptional(jobId)
        .orElseThrow(() -> new EntityNotFoundException("The job (id: " + jobId + ") could not be found"));
  }

  private boolean isMemberOfOrganization(UserDB user, OrganizationDB organization) {
    return organization.members.contains(user) || organization.owners.contains(user);
  }
}
