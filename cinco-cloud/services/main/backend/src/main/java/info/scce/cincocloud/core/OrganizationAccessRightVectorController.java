package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.OrganizationAccessRightVectorTO;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.rest.ObjectCache;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
@Path("/organization/{orgId}/accessRights")
@RequestScoped
public class OrganizationAccessRightVectorController {

  @Inject
  ObjectCache objectCache;

  @GET
  @Path("/")
  @RolesAllowed("user")
  public Response getAll(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB org = OrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (isOwnerOf(subject, org)) {
        final var accessRights = OrganizationAccessRightVectorDB.listAll().stream()
                .map(v -> (OrganizationAccessRightVectorDB) v)
                .map(v -> OrganizationAccessRightVectorTO.fromEntity(v, objectCache))
                .collect(Collectors.toList());

        return Response.ok(accessRights).build();
      } else {
        return Response.ok(new ArrayList<>()).build();
      }
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }


  @GET
  @Path("/my")
  @RolesAllowed("user")
  public Response get(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB org = OrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      final List<OrganizationAccessRightVectorDB> result = OrganizationAccessRightVectorDB
          .list("user = ?1 and organization = ?2", subject, org);
      if (result.size() == 1) {
        return Response.ok(OrganizationAccessRightVectorTO.fromEntity(result.get(0), objectCache))
            .build();
      }
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @PUT
  @Path("/{arvId}")
  @RolesAllowed("user")
  public Response update(
      @Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId,
      @PathParam("arvId") final long arvId,
      final OrganizationAccessRightVectorTO arv) {
    final UserDB subject = UserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final OrganizationDB org = OrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      final OrganizationAccessRightVectorDB arvInDb = OrganizationAccessRightVectorDB
          .findById(arvId);
      if (arvInDb == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      arvInDb.accessRights.clear();
      arvInDb.accessRights.addAll(arv.getaccessRights());
      arvInDb.persist();

      return Response.ok(OrganizationAccessRightVectorTO.fromEntity(arvInDb, objectCache))
          .build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isOwnerOf(UserDB user, OrganizationDB org) {
    return org.owners.contains(user);
  }
}
