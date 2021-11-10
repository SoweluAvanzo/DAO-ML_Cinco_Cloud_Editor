package info.scce.cincocloud.core;

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
import info.scce.cincocloud.core.rest.types.PyroWorkspaceImage;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.db.PyroWorkspaceImageDB;
import info.scce.cincocloud.rest.ObjectCache;

@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/image-registry")
@RequestScoped
public class WorkspaceImageController {

    @Inject
    ObjectCache objectCache;

    @GET
    @Path("/search")
    @RolesAllowed("user")
    public Response search(@Context SecurityContext securityContext, @QueryParam("q") String query) {
        final var currentUser = PyroUserDB.getCurrentUser(securityContext);

        final List<PyroWorkspaceImage> images = PyroWorkspaceImageDB.listAll()
                .stream()
                .map(image -> (PyroWorkspaceImageDB) image)
                .filter(image -> image.published || image.project.owner.equals(currentUser))
                .map(image -> PyroWorkspaceImage.fromEntity(image, objectCache))
                .collect(Collectors.toList());

        if (query == null || query.trim().isEmpty()) {
            return Response.ok(images).build();
        } else {
            final String q = query.toLowerCase();
            final List<PyroWorkspaceImage> filteredImages = images.stream()
                    .filter(image -> image.name.toLowerCase().contains(q)
                            || image.imageName.contains(q)
                            || image.user.getusername().toLowerCase().contains(q))
                    .collect(Collectors.toList());

            return Response.ok(filteredImages).build();
        }
    }

    @GET
    @Path("/images")
    @RolesAllowed("user")
    public Response getAll(@Context SecurityContext securityContext, @QueryParam("q") String query) {
        final var currentUser = PyroUserDB.getCurrentUser(securityContext);

        final var images = PyroWorkspaceImageDB.find("user", currentUser).list().stream()
                .map(i -> (PyroWorkspaceImageDB) i)
                .map(i -> PyroWorkspaceImage.fromEntity(i, objectCache))
                .collect(Collectors.toList());

        return Response.ok(images).build();
    }

    @PUT
    @Path("/images/{imageId}")
    @RolesAllowed("user")
    public Response update(@Context SecurityContext securityContext, @PathParam("imageId") long userId, PyroWorkspaceImage image) {
        final var currentUser = PyroUserDB.getCurrentUser(securityContext);

        final var imageInDb = (PyroWorkspaceImageDB) PyroWorkspaceImageDB.findByIdOptional(userId)
                .orElseThrow(() -> new EntityNotFoundException("Image could not be found."));

        if (!imageInDb.user.equals(currentUser)) {
            throw new ForbiddenException("You are not allowed to modify this image.");
        }

        imageInDb.updatedAt = Instant.now();
        imageInDb.published = image.published;
        imageInDb.persist();

        return Response.ok(PyroWorkspaceImage.fromEntity(imageInDb, objectCache)).build();
    }
}
