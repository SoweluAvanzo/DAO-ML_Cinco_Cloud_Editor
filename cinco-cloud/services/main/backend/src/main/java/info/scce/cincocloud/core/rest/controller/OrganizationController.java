package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UpdateOrganizationInput;
import info.scce.cincocloud.core.rest.inputs.UpdateOrganizationUsersInput;
import info.scce.cincocloud.core.rest.tos.BooleanTO;
import info.scce.cincocloud.core.rest.tos.OrganizationTO;
import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.services.OrganizationService;
import info.scce.cincocloud.core.services.ProjectService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.core.services.WorkspaceImageService;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/organizations")
@RequestScoped
public class OrganizationController {

  @Inject
  OrganizationService organizationService;

  @Inject
  UserService userService;

  @Inject
  ProjectService projectService;

  @Inject
  WorkspaceImageService workspaceImageService;

  @Inject
  ObjectCache objectCache;

  @POST
  @RolesAllowed("user")
  public Response create(@Context SecurityContext securityContext, OrganizationTO newOrg) {
    final var subject = UserService.getCurrentUser(securityContext);

    final OrganizationDB org = organizationService.create(newOrg.getname(), newOrg.getdescription(), subject);

    return Response.status(Status.CREATED).entity(OrganizationTO.fromEntity(org, objectCache)).build();
  }

  @GET
  @RolesAllowed("user")
  public Response getAll(
          @Context SecurityContext securityContext,
          @QueryParam("page") @DefaultValue("0") Integer index,
          @QueryParam("size") @DefaultValue("25") Integer size
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final var query = organizationService.getAllAccessibleOrganizations(subject);
    final var pageTO = PageTO.ofQuery(query, index, size, p -> OrganizationTO.fromEntity(p, objectCache));
    return Response.ok(pageTO).build();
  }

  @GET
  @Path("/{orgId}")
  @RolesAllowed("user")
  public Response get(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanAccessOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    return Response.ok(OrganizationTO.fromEntity(organization, objectCache)).build();
  }

  @POST
  @Path("/{orgId}/projects")
  @RolesAllowed("user")
  public Response createProject(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      ProjectTO projectTO) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (projectTO.getorganization() == null || projectTO.getorganization().getId() != orgId) {
      throw new RestException(Status.BAD_REQUEST, "Organization ID missing or malformed.");
    }

    if (!projectService.userCanCreateProject(subject, Optional.ofNullable(organization))) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final Optional<WorkspaceImageDB> imageOptional = Optional
        .ofNullable(projectTO.getTemplate())
        .map(i -> workspaceImageService.getOrThrow(i.getId()));

    if (imageOptional.isPresent()) {
      final var image = imageOptional.get();

      if (!image.published && !projectService.userHasOwnerStatus(subject, image.project)) {
        throw new RestException(Response.Status.BAD_REQUEST, "You are not allowed to use this image.");
      }
    }

    final ProjectDB project = projectService.createProject(
        projectTO.getname(),
        projectTO.getdescription(),
        subject,
        Optional.of(organization),
        imageOptional
    );

    return Response.status(Status.CREATED).entity(ProjectTO.fromEntity(project, objectCache)).build();
  }

  @PUT
  @Path("/{orgId}/rpc/leave")
  @RolesAllowed("user")
  public Response leave(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    organizationService.removeUserFromOrganization(subject, organization);

    return Response.ok().build();
  }

  @POST
  @Path("/{orgId}/members")
  @RolesAllowed("user")
  public Response makeMember(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      @Valid UpdateOrganizationUsersInput input) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final UserDB user = userService.getOrThrow(input.getUserId());

    final var updatedOrganization = organizationService.makeMember(user, organization);

    return Response.ok(OrganizationTO.fromEntity(updatedOrganization, objectCache)).build();
  }

  @POST
  @Path("/{orgId}/owners")
  @RolesAllowed("user")
  public Response makeOwner(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      @Valid UpdateOrganizationUsersInput input) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final UserDB user = userService.getOrThrow(input.getUserId());

    final var updatedOrganization = organizationService.makeOwner(user, organization);

    return Response.ok(OrganizationTO.fromEntity(updatedOrganization, objectCache)).build();
  }

  @DELETE
  @Path("/{orgId}/users/{userId}")
  @RolesAllowed("user")
  public Response removeUser(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId, @PathParam("userId") final long userId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final UserDB user = userService.getOrThrow(userId);

    final var updatedOrganization = organizationService.removeUserFromOrganization(user, organization);

    return Response.ok(OrganizationTO.fromEntity(updatedOrganization, objectCache)).build();
  }

  @PUT
  @Path("/{orgId}")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext,
                         @PathParam("orgId") final long orgId,
                         @Valid UpdateOrganizationInput input) {
    final var subject = UserService.getCurrentUser(securityContext);
    final var updatedOrganization = organizationService.updateOrganization(subject, orgId, input);
    return Response.ok(OrganizationTO.fromEntity(updatedOrganization, objectCache)).build();
  }

  @DELETE
  @Path("/{orgId}")
  @RolesAllowed("user")
  public Response delete(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    organizationService.delete(organization);

    return Response.ok().build();
  }

  @GET
  @Path("/{orgId}/rpc/has-active-build-jobs")
  @RolesAllowed("user")
  public Response hasActiveBuildJobs(
      @Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId
  ) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    return Response.ok(new BooleanTO(organizationService.hasActiveBuildJobs(organization))).build();
  }

}
