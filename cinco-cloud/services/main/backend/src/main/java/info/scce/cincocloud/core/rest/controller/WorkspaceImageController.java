package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.inputs.UpdateWorkspaceImageInput;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageTO;
import info.scce.cincocloud.core.services.UserService;
import info.scce.cincocloud.core.services.WorkspaceImageService;
import info.scce.cincocloud.exeptions.RestException;
import info.scce.cincocloud.rest.ObjectCache;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

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
  public Response getAll(@Context SecurityContext securityContext, @QueryParam("search") String search) {
    final var subject = UserService.getCurrentUser(securityContext);

    final var result = search == null ?
        workspaceImageService.getAllAccessibleImages(subject) :
        workspaceImageService.searchAllAccessibleImages(subject, search);

    return Response.ok(result.stream().map(i -> WorkspaceImageTO.fromEntity(i, objectCache))
        .collect(Collectors.toList())).build();
  }

  @PUT
  @Path("/{imageId}")
  @RolesAllowed("user")
  public Response update(@Context SecurityContext securityContext,
                         @PathParam("imageId") long imageId,
                         UpdateWorkspaceImageInput input) {
    final var subject = UserService.getCurrentUser(securityContext);

    if (!workspaceImageService.userCanModifyImage(subject, imageId)) {
      throw new RestException(Status.FORBIDDEN, "You are not allowed to modify this image.");
    }

    final var updatedImage = workspaceImageService.setPublished(imageId, input.published);

    return Response.ok(WorkspaceImageTO.fromEntity(updatedImage, objectCache)).build();
  }
}
