package info.scce.cincocloud.core.services;

import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.WorkspaceImageDB;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class WorkspaceImageService {

  @Inject
  ProjectService projectService;

  public WorkspaceImageDB getOrThrow(long imageId) {
    return (WorkspaceImageDB) WorkspaceImageDB.findByIdOptional(imageId)
        .orElseThrow(() -> new EntityNotFoundException("Image could not be found."));
  }

  public List<WorkspaceImageDB> getAllAccessibleImages(UserDB subject) {
    return WorkspaceImageDB.findAllWhereProjectIsNotDeleted()
        .stream()
        .filter(image -> userCanAccessImage(subject, image))
        .collect(Collectors.toList());
  }

  public List<WorkspaceImageDB> searchAllAccessibleImages(UserDB subject, String searchQuery) {
    List<WorkspaceImageDB> foundImages = getAllAccessibleImages(subject);
    if (searchQuery == null || searchQuery.trim().isEmpty()) {
      return foundImages;
    }

    final String q = searchQuery.toLowerCase();
    return foundImages.stream()
        .filter(image ->
                image.project.name.toLowerCase().contains(q) ||
                    image.project.matchOnOwnership(
                        owner -> owner.name.toLowerCase().contains(q),
                        organization -> organization.name.toLowerCase().contains(q)
                    )
        )
        .collect(Collectors.toList());
  }

  public WorkspaceImageDB setPublished(long imageId, boolean published) {
    final var updatedImage = getOrThrow(imageId);

    updatedImage.published = published;
    updatedImage.updatedAt = Instant.now();

    return updatedImage;
  }

  public boolean userCanAccessImage(UserDB subject, WorkspaceImageDB image) {
    return image.published || image.project.matchOnOwnership(
        owner -> owner.equals(subject),
        org -> org.members.contains(subject) || org.owners.contains(subject));
  }

  public boolean userCanModifyImage(UserDB subject, long imageId) {
    return projectService.userHasOwnerStatus(subject, getOrThrow(imageId).project);
  }
}
