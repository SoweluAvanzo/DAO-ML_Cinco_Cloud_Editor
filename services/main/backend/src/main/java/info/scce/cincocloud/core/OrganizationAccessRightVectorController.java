package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.types.PyroOrganizationAccessRightVector;
import info.scce.cincocloud.db.PyroOrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import java.util.List;
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
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      if (isOwnerOf(subject, org)) {
        final List<PyroOrganizationAccessRightVectorDB> result = PyroOrganizationAccessRightVectorDB
            .listAll();

        final List<PyroOrganizationAccessRightVector> arvs = new java.util.ArrayList<>();
        for (PyroOrganizationAccessRightVectorDB arv : result) {
          arvs.add(PyroOrganizationAccessRightVector.fromEntity(arv, objectCache));
        }

        return Response.ok(arvs).build();
      }
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }


  @GET
  @Path("/my")
  @RolesAllowed("user")
  public Response get(@Context SecurityContext securityContext,
      @PathParam("orgId") final long orgId) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      final List<PyroOrganizationAccessRightVectorDB> result = PyroOrganizationAccessRightVectorDB
          .list("user = ?1 and organization = ?2", subject, org);
      if (result.size() == 1) {
        return Response.ok(PyroOrganizationAccessRightVector.fromEntity(result.get(0), objectCache))
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
      final PyroOrganizationAccessRightVector arv) {
    final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

    if (subject != null) {
      final PyroOrganizationDB org = PyroOrganizationDB.findById(orgId);
      if (org == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      final PyroOrganizationAccessRightVectorDB arvInDb = PyroOrganizationAccessRightVectorDB
          .findById(arvId);
      if (arvInDb == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      arvInDb.accessRights.clear();
      arvInDb.accessRights.addAll(arv.getaccessRights());
      arvInDb.persist();

      return Response.ok(PyroOrganizationAccessRightVector.fromEntity(arvInDb, objectCache))
          .build();
    }

    return Response.status(Response.Status.FORBIDDEN).build();
  }

  private boolean isOwnerOf(PyroUserDB user, PyroOrganizationDB org) {
    return org.owners.contains(user);
  }
}
