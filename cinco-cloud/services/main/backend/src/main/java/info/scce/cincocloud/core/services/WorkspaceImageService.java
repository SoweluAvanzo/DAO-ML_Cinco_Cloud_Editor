package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.inputs.UpdateWorkspaceImageInput;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import info.scce.cincocloud.exeptions.RestException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Transactional
public class WorkspaceImageService {

  @Inject
  ProjectService projectService;

  public WorkspaceImageDB getOrThrow(long imageId) {
    return (WorkspaceImageDB) WorkspaceImageDB.findByIdOptional(imageId)
        .orElseThrow(() -> new EntityNotFoundException("Image could not be found."));
  }

  public WorkspaceImageDB updateImage(UserDB user, Long imageId, UpdateWorkspaceImageInput input) {
    final var image = getOrThrow(imageId);

    if (!userCanModifyImage(user, image)) {
      throw new RestException(Response.Status.FORBIDDEN, "You are not allowed to modify this image.");
    }

    image.published = input.published;
    image.updatedAt = Instant.now();

    if (user.isAdmin() && input.featured != null) {
      image.featured = input.featured;
    }

    return image;
  }

  public boolean userCanAccessImage(UserDB subject, WorkspaceImageDB image) {
    return image.published || image.project.matchOnOwnership(
        owner -> owner.equals(subject),
        org -> org.members.contains(subject) || org.owners.contains(subject));
  }

  public boolean userCanModifyImage(UserDB subject, WorkspaceImageDB image) {
    return subject.isAdmin() || projectService.userHasOwnerStatus(subject, image.project);
  }
}
