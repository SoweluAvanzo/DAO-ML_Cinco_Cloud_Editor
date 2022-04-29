package info.scce.cincocloud.core;

import info.scce.cincocloud.core.rest.tos.WorkspaceImageTO;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/image-registry")
@RequestScoped
public class WorkspaceImageController {

  @Inject
  ObjectCache objectCache;

  @Inject
  ProjectService projectService;

  @GET
  @Path("/search")
  @RolesAllowed("user")
  public Response search(@Context SecurityContext securityContext, @QueryParam("q") String query) {
    final var currentUser = UserDB.getCurrentUser(securityContext);

    final List<WorkspaceImageDB> images = WorkspaceImageDB.listAll()
        .stream()
        .map(image -> (WorkspaceImageDB) image)
        .filter(image -> image.published || projectService.userOwnsProject(currentUser, image.project))
        .collect(Collectors.toList());

    List<WorkspaceImageDB> responseImages;
    if (query == null || query.trim().isEmpty()) {
      responseImages = images;
    } else {
      final String q = query.toLowerCase();
      responseImages =
          images.stream()
              .filter(image ->
                  image.project.name.toLowerCase().contains(q) ||
                      image.project.matchOnOwnership(
                          owner -> owner.name.toLowerCase().contains(q),
                          organization -> organization.name.toLowerCase().contains(q)
                      )
              )
              .collect(Collectors.toList());
    }

    return Response.ok(
        responseImages
            .stream()
            .map(image -> WorkspaceImageTO.fromEntity(image, objectCache))
            .collect(Collectors.toList())
    ).build();
  }

  @GET
  @Path("/images")
  @RolesAllowed("user")
  public Response getAll(@Context SecurityContext securityContext, @QueryParam("q") String query) {
    final var currentUser = UserDB.getCurrentUser(securityContext);

    final var images = WorkspaceImageDB.find("user", currentUser).list().stream()
        .map(i -> (WorkspaceImageDB) i)
        .map(i -> WorkspaceImageTO.fromEntity(i, objectCache))
        .collect(Collectors.toList());

    return Response.ok(images).build();
  }

  @PUT
  @Path("/images/{imageId}")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext,
      @PathParam("imageId") long userId, WorkspaceImageTO image) {
    final var currentUser = UserDB.getCurrentUser(securityContext);

    final var imageInDb = (WorkspaceImageDB) WorkspaceImageDB.findByIdOptional(userId)
        .orElseThrow(() -> new EntityNotFoundException("Image could not be found."));

    if (!projectService.userOwnsProject(currentUser, imageInDb.project)) {
      throw new ForbiddenException("You are not allowed to modify this image.");
    }

    imageInDb.updatedAt = Instant.now();
    imageInDb.published = image.published;
    imageInDb.persist();

    return Response.ok(WorkspaceImageTO.fromEntity(imageInDb, objectCache)).build();
  }
}
