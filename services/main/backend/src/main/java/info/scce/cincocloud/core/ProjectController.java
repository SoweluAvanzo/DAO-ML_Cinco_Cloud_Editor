package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.db.OrganizationAccessRight;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.ProjectType;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
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

    if (newProject.getorganization() == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    final OrganizationDB org = OrganizationDB
        .findById(newProject.getorganization().getId());
    if (org == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    final Optional<WorkspaceImageDB> imageOptional = Optional
        .ofNullable(newProject.getTemplate())
        .map(i -> WorkspaceImageDB.findById(i.getId()));

    if (imageOptional.isPresent()) {
      final WorkspaceImageDB image = imageOptional.get();
      if (!image.published && !image.project.owner.equals(subject)) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
    }

    if (canCreateProject(subject, org)) {
      final ProjectDB pp = createProject(
          newProject.getname(),
          newProject.getdescription(),
          subject,
          org,
          imageOptional
      );

      return Response.ok(ProjectTO.fromEntity(pp, objectCache)).build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  public ProjectDB createProject(
      String name,
      String description,
      UserDB subject,
      OrganizationDB org,
      Optional<WorkspaceImageDB> image
  ) {
    final ProjectDB pp = new ProjectDB();
    pp.owner = subject;
    pp.name = name;
    pp.description = description;
    pp.organization = org;
    subject.ownedProjects.add(pp);
    org.projects.add(pp);

    image.ifPresent(i -> {
      pp.template = i;
      pp.type = ProjectType.MODEL_EDITOR;
    });

    pp.persist();
    subject.persist();
    org.persist();

    return pp;
  }

  @POST
  @Path("/update/private")
  @RolesAllowed("user")
  public Response updateProject(@Context SecurityContext securityContext,
      ProjectTO ownedProject) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    final ProjectDB pp = ProjectDB.findById(ownedProject.getId());
    projectService.checkPermission(pp, securityContext);

    if (canEditProject(subject, pp)) {
      pp.description = ownedProject.getdescription();
      pp.name = ownedProject.getname();

      // set new owner
      if (pp.organization.owners.contains(subject)
          || subject.systemRoles.size() > 0
          || pp.owner.equals(subject)
      ) {
        final UserDB newOwner = UserDB.findById(ownedProject.getowner().getId());
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
      return Response.ok(ProjectTO.fromEntity(pp, objectCache)).build();
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
    projectService.checkPermission(project, securityContext);

    if (isInOrganization(subject, project.organization)) {
      return Response.ok(ProjectTO.fromEntity(project, objectCache)).build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/{projectId}/deployments/private")
  @RolesAllowed("user")
  public Response deployProject(@Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId) {
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkPermission(project, securityContext);
    final var result = projectDeploymentService.deploy(project);
    return Response.ok(result).build();
  }

  @DELETE
  @Path("/{projectId}/deployments/private")
  @RolesAllowed("user")
  public Response stopDeployedProject(@Context SecurityContext securityContext,
      @PathParam("projectId") final long projectId) {
    final ProjectDB project = ProjectDB.findById(projectId);
    projectService.checkPermission(project, securityContext);
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
    if (canDeleteProject(subject, project)) {
      projectService.deleteById(subject, id, securityContext);
      projectDeploymentService.delete(project);
      return Response.ok("Removed").build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isInOrganization(
      UserDB user,
      OrganizationDB org) {
    return org.members.contains(user) || org.owners.contains(user);
  }

  private boolean canCreateProject(
      UserDB user,
      OrganizationDB org) {
    OrganizationAccessRightVectorDB arv = getAccessRightVector(user, org);
    return arv != null && arv.accessRights.contains(OrganizationAccessRight.CREATE_PROJECTS);
  }

  private boolean canEditProject(
      UserDB user,
      ProjectDB project) {
    OrganizationAccessRightVectorDB arv = getAccessRightVector(user, project);
    return arv != null && arv.accessRights.contains(OrganizationAccessRight.EDIT_PROJECTS);
  }

  private boolean canDeleteProject(
      UserDB user,
      ProjectDB project) {
    OrganizationAccessRightVectorDB arv = getAccessRightVector(user, project);
    return arv != null && arv.accessRights.contains(OrganizationAccessRight.DELETE_PROJECTS);
  }

  private OrganizationAccessRightVectorDB getAccessRightVector(
      UserDB user,
      ProjectDB project
  ) {
    return getAccessRightVector(user, project.organization);
  }

  private OrganizationAccessRightVectorDB getAccessRightVector(
      UserDB user,
      OrganizationDB org
  ) {
    final List<OrganizationAccessRightVectorDB> result = OrganizationAccessRightVectorDB
        .list("user = ?1 and organization = ?2", user, org);
    return result.size() == 1 ? result.get(0) : null;
  }
}
