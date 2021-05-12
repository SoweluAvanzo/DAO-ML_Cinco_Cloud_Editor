package info.scce.cincocloud.core;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.PyroWorkspaceImage;
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
    @Path("/images")
    @RolesAllowed("user")
    public Response getAll(@QueryParam("q") String query, SecurityContext securityContext) {
        final List<PyroWorkspaceImage> images = PyroWorkspaceImageDB.listAll()
                .stream()
                .map(image -> (PyroWorkspaceImageDB) image)
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
}
