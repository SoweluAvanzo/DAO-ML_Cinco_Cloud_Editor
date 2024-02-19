package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.tos.OrganizationAccessRightVectorTO;
import info.scce.cincocloud.core.services.OrganizationAccessRightVectorService;
import info.scce.cincocloud.core.services.OrganizationService;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Collectors;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
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
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/organizations/{orgId}/access-rights")
@RequestScoped
public class OrganizationAccessRightVectorController {

  @Inject
  ObjectCache objectCache;

  @Inject
  OrganizationService organizationService;

  @Inject
  OrganizationAccessRightVectorService organizationAccessRightVectorService;

  @Inject
  UserService userService;

  @GET
  @RolesAllowed("user")
  public Response getAll(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      @QueryParam("user-id") final String userId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB org = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, org)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    if (userId != null) {
      long userIdL;

      try {
        userIdL = Long.parseLong(userId);
      } catch (NumberFormatException e) {
        throw new RestException(Status.BAD_REQUEST, "Invalid user ID.");
      }

      final var user = userService.getOrThrow(userIdL);
      final var oarv = organizationAccessRightVectorService.getByUserAndOrganization(user, org);

      return Response.ok(OrganizationAccessRightVectorTO.fromEntity(oarv, objectCache)).build();
    }

    final var result = organizationAccessRightVectorService.getAll(org).stream()
        .map(i -> OrganizationAccessRightVectorTO.fromEntity(i, objectCache))
        .collect(Collectors.toList());

    return Response.ok(result).build();
  }

  @GET
  @Path("/{userId}")
  @RolesAllowed("user")
  public Response get(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      @PathParam("userId") final long userId) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB org = organizationService.getOrThrow(orgId);
    final UserDB user = userService.getOrThrow(userId);

    if (!organizationService.userCanAccessOrganization(user, org)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final var result = organizationAccessRightVectorService.getByUserAndOrganization(subject, org);

    return Response.ok(OrganizationAccessRightVectorTO.fromEntity(result, objectCache)).build();
  }

  @PUT
  @Path("/{arvId}")
  @RolesAllowed("user")
  public Response update(
      @Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      @PathParam("arvId") final long arvId,
      final OrganizationAccessRightVectorTO arvTO) {
    final var subject = UserService.getCurrentUser(securityContext);
    final OrganizationDB org = organizationService.getOrThrow(orgId);

    if (!organizationService.userCanEditOrganization(subject, org)) {
      throw new RestException(Status.FORBIDDEN, "Insufficient access rights.");
    }

    final OrganizationAccessRightVectorDB arv = organizationAccessRightVectorService.getOrThrow(arvId);

    organizationAccessRightVectorService.setAccessRights(arv, arvTO.getaccessRights());

    return Response.ok(OrganizationAccessRightVectorTO.fromEntity(arv, objectCache)).build();
  }
}
