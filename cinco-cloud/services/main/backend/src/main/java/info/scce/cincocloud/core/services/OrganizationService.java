package info.scce.cincocloud.core.services;

import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class OrganizationService {
  @Inject
  ProjectService projectService;

  @Inject
  OrganizationAccessRightVectorService organizationAccessRightVectorService;

  public OrganizationDB getOrThrow(long organizationId) {
      return (OrganizationDB) OrganizationDB.findByIdOptional(organizationId)
          .orElseThrow(() -> new EntityNotFoundException("Cannot find organization."));
  }

  public List<OrganizationDB> getAllAccessibleOrganizations(UserDB subject) {
    return OrganizationDB.findOrganizationsWhereUserIsOwnerOrMember(subject.id).list();
  }

  public OrganizationDB create(String name, String description, UserDB subject) {
    final var nameExists =
        !OrganizationDB.list("name", name).isEmpty() ||
            !UserDB.list("username", name).isEmpty();
    if (nameExists) {
      throw new RestException("The name already exists");
    }

    final OrganizationDB org = new OrganizationDB();
    org.name = name;
    org.description = description;
    org.persist();

    if (subject != null) {
      org.owners.add(subject);
      final var oarv = organizationAccessRightVectorService.create(subject, org);
      organizationAccessRightVectorService.addOwnerRights(oarv);
    }

    return org;
  }

  public OrganizationDB updateName(OrganizationDB organization, String name) {
    organization.name = name;

    return organization;
  }

  public OrganizationDB updateDescription(OrganizationDB organization, String description) {
    organization.description = description;

    return organization;
  }

  public OrganizationDB updateLogo(OrganizationDB organization, Optional<Long> logoIdOptional) {
    organization.logo = logoIdOptional.isPresent() ? BaseFileDB.findById(logoIdOptional.get()) : null;

    return organization;
  }

  public void delete(OrganizationDB organization) {
    if (organization.projects.stream().anyMatch(ProjectDB::hasActiveBuildjobs)) {
      throw new IllegalArgumentException("There is at least one project with active buildjobs.");
    }

    deleteAllProjects(organization);
    deleteAccessRightVectors(organization);

    organization.members.clear();
    organization.owners.clear();
    organization.projects.clear();
    organization.delete();
  }

  public OrganizationDB removeUserFromOrganization(UserDB user, OrganizationDB organization) {
    if (organization.owners.contains(user) && organization.owners.size() == 1) {
      throw new IllegalArgumentException("Cannot remove the only owner of an organization.");
    }
    if (!organization.owners.contains(user) && !organization.members.contains(user)) {
      throw new IllegalArgumentException("Cannot remove user from an organization he is not part of.");
    }

    if (organization.owners.contains(user)) {
      organizationAccessRightVectorService.deleteByUserAndOrganization(user, organization);
      organization.owners.remove(user);
    } else if (organization.members.contains(user)) {
      organizationAccessRightVectorService.deleteByUserAndOrganization(user, organization);
      organization.members.remove(user);
    }

    organization.persist();
    user.persist();

    return organization;
  }

  public OrganizationDB makeMember(UserDB user, OrganizationDB organization) {
    if (organization.owners.contains(user)) {
      if (organization.owners.size() == 1) {
        throw new IllegalArgumentException("Cannot demote the only organization owner to member.");
      }
      organization.owners.remove(user);
    }

    final var accessRightVectorOptional =
        organizationAccessRightVectorService.getByUserAndOrganizationOptional(user, organization);

    accessRightVectorOptional.ifPresentOrElse(
        organizationAccessRightVectorDB -> organizationAccessRightVectorService.removeOwnerRights(organizationAccessRightVectorDB),
        () -> organizationAccessRightVectorService.create(user, organization)
    );

    organization.members.add(user);
    organization.persist();

    return organization;
  }

  public OrganizationDB makeOwner(UserDB user, OrganizationDB organization) {
    organization.members.remove(user);

    final var accessRIghtVevtorOptional =
        organizationAccessRightVectorService.getByUserAndOrganizationOptional(user, organization);

    accessRIghtVevtorOptional.ifPresentOrElse(
        organizationAccessRightVectorDB -> organizationAccessRightVectorService.addOwnerRights(organizationAccessRightVectorDB),
        () -> organizationAccessRightVectorService.create(user, organization)
    );

    organization.owners.add(user);
    organization.persist();

    return organization;
  }

  public boolean hasActiveBuildJobs(OrganizationDB organization) {
    return organization.projects.stream().anyMatch(ProjectDB::hasActiveBuildjobs);
  }

  public boolean userCanAccessOrganization(UserDB subject, OrganizationDB organization) {
    return organization.members.contains(subject) || organization.owners.contains(subject);
  }

  public boolean userCanEditOrganization(UserDB subject, OrganizationDB organization) {
    return organization.owners.contains(subject);
  }

  private void deleteAllProjects(OrganizationDB org) {
    Iterator<ProjectDB> iter = org.projects.iterator();
    while (iter.hasNext()) {
      ProjectDB project = iter.next();
      projectService.deleteProject(project);
      iter = org.projects.iterator();
    }
  }

  private void deleteAccessRightVectors(OrganizationDB org) {
    List<OrganizationAccessRightVectorDB> accessRightVectors = OrganizationAccessRightVectorDB.findAccessRightVectors(org).list();
    for (OrganizationAccessRightVectorDB vector : accessRightVectors) {
      vector.accessRights.clear();
      vector.organization = null;
      vector.user = null;
      vector.persist();
      vector.delete();
    }
  }
}
