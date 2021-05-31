package info.scce.cincocloud.core;

import java.util.Optional;
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
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.PyroProject;
import info.scce.cincocloud.db.PyroOrganizationAccessRightDB;
import info.scce.cincocloud.db.PyroOrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.db.PyroWorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;

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
    public Response createProject(@Context SecurityContext securityContext, PyroProject newProject) {

        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        if (newProject.getorganization() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final PyroOrganizationDB org = PyroOrganizationDB.findById(newProject.getorganization().getId());
        if (org == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Optional<PyroWorkspaceImageDB> image = Optional.ofNullable(newProject.getimage())
                .map(i -> PyroWorkspaceImageDB.findById(i.getId()));

        if (canCreateProject(subject, org)) {
            final PyroProjectDB pp = createProject(
                    newProject.getname(),
                    newProject.getdescription(),
                    subject,
                    org,
                    image
            );
            return Response.ok(PyroProject.fromEntity(pp, objectCache)).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public PyroProjectDB createProject(
            String name,
            String description,
            PyroUserDB subject,
            PyroOrganizationDB org,
            Optional<PyroWorkspaceImageDB> image
    ) {
        final PyroProjectDB pp = new PyroProjectDB();
        pp.owner = subject;
        pp.name = name;
        pp.description = description;
        pp.organization = org;
        subject.ownedProjects.add(pp);
        org.projects.add(pp);
        image.ifPresent(i -> pp.image = i);

        pp.persist();
        subject.persist();
        org.persist();

        return pp;
    }

    @POST
    @Path("/update/private")
    @RolesAllowed("user")
    public Response updateProject(@Context SecurityContext securityContext, PyroProject ownedProject) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        final PyroProjectDB pp = PyroProjectDB.findById(ownedProject.getId());
        projectService.checkPermission(pp, securityContext);

        if (canEditProject(subject, pp)) {
            pp.description = ownedProject.getdescription();
            pp.name = ownedProject.getname();

            // set new owner
            if (pp.organization.owners.contains(subject)
                    || subject.systemRoles.size() > 0
                    || pp.owner.equals(subject)
            ) {
                final PyroUserDB newOwner = PyroUserDB.findById(ownedProject.getowner().getId());
                if (newOwner == null) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                if (!isInOrganization(newOwner, pp.organization)) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                pp.owner.ownedProjects.remove(pp);
                pp.owner = newOwner;
                pp.persist();
                newOwner.ownedProjects.add(pp);
                newOwner.persist();
            }
            pp.persist();
            return Response.ok(PyroProject.fromEntity(pp, objectCache)).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @GET
    @Path("/{projectId}")
    @RolesAllowed("user")
    public Response getProject(@Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        final PyroProjectDB project = PyroProjectDB.findById(projectId);
        projectService.checkPermission(project, securityContext);

        if (isInOrganization(subject, project.organization)) {
            return Response.ok(PyroProject.fromEntity(project, objectCache)).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Path("/{projectId}/deployments/private")
    @RolesAllowed("user")
    public Response deployProject(@Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
        final PyroProjectDB project = PyroProjectDB.findById(projectId);
        projectService.checkPermission(project, securityContext);
        final var result = projectDeploymentService.deploy(project);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{projectId}/deployments/private")
    @RolesAllowed("user")
    public Response stopDeployedProject(@Context SecurityContext securityContext, @PathParam("projectId") final long projectId) {
        final PyroProjectDB project = PyroProjectDB.findById(projectId);
        projectService.checkPermission(project, securityContext);
        projectDeploymentService.stop(project);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/remove/{id}/private")
    @RolesAllowed("user")
    public Response removeProject(@Context SecurityContext securityContext, @PathParam("id") final long id) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);
        final PyroProjectDB project = PyroProjectDB.findById(id);
        if (canDeleteProject(subject, project)) {
            projectService.deleteById(subject, id, securityContext);
            projectDeploymentService.delete(project);
            return Response.ok("Removed").build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    private boolean isInOrganization(
            PyroUserDB user,
            PyroOrganizationDB org) {
        return org.members.contains(user) || org.owners.contains(user);
    }

    private boolean canCreateProject(
            PyroUserDB user,
            PyroOrganizationDB org) {
        PyroOrganizationAccessRightVectorDB arv = getAccessRightVector(user, org);
        return arv != null && arv.accessRights.contains(PyroOrganizationAccessRightDB.CREATE_PROJECTS);
    }

    private boolean canEditProject(
            PyroUserDB user,
            PyroProjectDB project) {
        PyroOrganizationAccessRightVectorDB arv = getAccessRightVector(user, project);
        return arv != null && arv.accessRights.contains(PyroOrganizationAccessRightDB.EDIT_PROJECTS);
    }

    private boolean canDeleteProject(
            PyroUserDB user,
            PyroProjectDB project) {
        PyroOrganizationAccessRightVectorDB arv = getAccessRightVector(user, project);
        return arv != null && arv.accessRights.contains(PyroOrganizationAccessRightDB.DELETE_PROJECTS);
    }

    private PyroOrganizationAccessRightVectorDB getAccessRightVector(
            PyroUserDB user,
            PyroProjectDB project
    ) {
        return getAccessRightVector(user, project.organization);
    }

    private PyroOrganizationAccessRightVectorDB getAccessRightVector(
            PyroUserDB user,
            PyroOrganizationDB org
    ) {
        final java.util.List<PyroOrganizationAccessRightVectorDB> result = PyroOrganizationAccessRightVectorDB.list("user = ?1 and organization = ?2", user, org);
        return result.size() == 1 ? result.get(0) : null;
    }
}
