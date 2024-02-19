package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UpdateWorkspaceImageInput;
import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageTO;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.core.services.WorkspaceImageService;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/images")
@RequestScoped
public class WorkspaceImageController {

  @Inject
  ObjectCache objectCache;

  @Inject
  WorkspaceImageService workspaceImageService;

  @GET
  @RolesAllowed("user")
  public Response getAll(@Context SecurityContext securityContext,
                         @QueryParam("search") Optional<String> search,
                         @QueryParam("featured") Optional<Boolean> featured,
                         @QueryParam("page") @DefaultValue("0") Integer pageIndex,
                         @QueryParam("size") @DefaultValue("25") Integer pageSize) {
    final var subject = UserService.getCurrentUser(securityContext);

    final PanacheQuery<WorkspaceImageDB> images;
    if (featured.isPresent()) {
      images = WorkspaceImageDB.findAllFeaturedImages();
    } else {
      images = WorkspaceImageDB.findAllAccessibleImages(subject, search);
    }

    final var items = images.list();
    final var pageTO = PageTO.ofList(items, pageIndex, pageSize, i -> WorkspaceImageTO.fromEntity(i, objectCache));
    return Response.ok(pageTO).build();
  }

  @PUT
  @Path("/{imageId}")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext,
                         @PathParam("imageId") long imageId,
                         UpdateWorkspaceImageInput input) {
    final var subject = UserService.getCurrentUser(securityContext);
    final var updatedImage = workspaceImageService.updateImage(subject, imageId, input);
    return Response.ok(WorkspaceImageTO.fromEntity(updatedImage, objectCache)).build();
  }
}
