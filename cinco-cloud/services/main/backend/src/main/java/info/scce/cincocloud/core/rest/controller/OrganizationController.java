package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UpdateOrganizationUsersInput;
import info.scce.cincocloud.core.rest.tos.BooleanTO;
import info.scce.cincocloud.core.rest.tos.OrganizationTO;
import info.scce.cincocloud.core.services.OrganizationService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/organizations")
@RequestScoped
public class OrganizationController {

  @Inject
  OrganizationService organizationService;

  @Inject
  UserService userService;

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
  public Response getAll(@Context SecurityContext securityContext) {
    final var subject = UserService.getCurrentUser(securityContext);

    final List<OrganizationDB> result = organizationService.getAllAccessibleOrganizations(subject);

    return Response.ok(result.stream().map(o -> OrganizationTO.fromEntity(o, objectCache)).collect(Collectors.toList())).build();
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
      @PathParam("orgId") final long orgId, OrganizationTO organizationTO) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB organization = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, organization)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    if ((organization.id != organizationTO.getId()) || organizationTO.getname().trim().equals("")) {
      throw new RestException(Response.Status.BAD_REQUEST, "Could not update organization.\n"
              + "ID or name does not match the database");
    }

    organizationService.updateName(organization, organizationTO.getname());
    organizationService.updateDescription(organization, organizationTO.getdescription());
    organizationService.updateLogo(organization, Optional.ofNullable(organizationTO.getlogo() != null ? organizationTO.getlogo().getId() : null));

    return Response.ok(OrganizationTO.fromEntity(organization, objectCache)).build();
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
