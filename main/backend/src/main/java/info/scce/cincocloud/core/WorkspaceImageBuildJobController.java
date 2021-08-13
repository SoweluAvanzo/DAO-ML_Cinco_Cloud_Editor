package info.scce.cincocloud.core;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.PyroWorkspaceImageBuildJob;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.db.PyroWorkspaceImageBuildJobDB;
import info.scce.cincocloud.rest.ObjectCache;

@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/projects/{projectId}")
@RequestScoped
public class WorkspaceImageBuildJobController {

    @Inject
    ObjectCache objectCache;

    @GET
    @Path("/build-jobs/private")
    @RolesAllowed("user")
    public Response getAll(
            @Context SecurityContext securityContext,
            @PathParam("projectId") final Long projectId,
            @QueryParam("page") @DefaultValue("0") final int page,
            @QueryParam("size") @DefaultValue("25") final int size
    ) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        final Optional<PyroProjectDB> projectOptional = PyroProjectDB.findByIdOptional(projectId);

        if (projectOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (!isMemberOfOrganization(subject, projectOptional.get().organization)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        final var jobs = PyroWorkspaceImageBuildJobDB.findByProjectId(projectId, Sort.ascending("startedAt"))
                .page(Page.of(page, size)).stream()
                .map(
                    job -> PyroWorkspaceImageBuildJob.fromEntity(job, objectCache)
                )
                .collect(Collectors.toList());

        return Response.ok(jobs).build();
    }

    @DELETE
    @Path("/build-jobs/{jobId}/private")
    @RolesAllowed("user")
    public Response delete(
            @Context SecurityContext securityContext,
            @PathParam("projectId") final Long projectId,
            @PathParam("jobId") final Long jobId
    ) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        final Optional<PyroWorkspaceImageBuildJobDB> jobOptional = PyroWorkspaceImageBuildJobDB.findByIdOptional(jobId);

        if (jobOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final var job = jobOptional.get();
        if (!job.project.owner.equals(subject)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (job.status.equals(PyroWorkspaceImageBuildJobDB.Status.PENDING)
                || job.status.equals(PyroWorkspaceImageBuildJobDB.Status.BUILDING)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
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
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        final Optional<PyroWorkspaceImageBuildJobDB> jobOptional = PyroWorkspaceImageBuildJobDB.findByIdOptional(jobId);

        if (jobOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final var job = jobOptional.get();
        if (!job.project.owner.equals(subject)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (!(job.status.equals(PyroWorkspaceImageBuildJobDB.Status.PENDING)
                || job.status.equals(PyroWorkspaceImageBuildJobDB.Status.BUILDING))) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        job.status = PyroWorkspaceImageBuildJobDB.Status.ABORTED;
        job.persist();

        return Response.ok(job).build();
    }

    private boolean isMemberOfOrganization(PyroUserDB user, PyroOrganizationDB organization) {
        return organization.members.contains(user) || organization.owners.contains(user);
    }
}
