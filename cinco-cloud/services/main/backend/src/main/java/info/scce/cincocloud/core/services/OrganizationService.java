package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.inputs.UpdateOrganizationInput;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestException;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Transactional
public class OrganizationService {

  @Inject
  ProjectService projectService;

  @Inject
  FileService fileService;

  @Inject
  OrganizationAccessRightVectorService organizationAccessRightVectorService;

  public OrganizationDB getOrThrow(long organizationId) {
      return (OrganizationDB) OrganizationDB.findByIdOptional(organizationId)
          .orElseThrow(() -> new EntityNotFoundException("Cannot find organization."));
  }

  public List<OrganizationDB> getAllAccessibleOrganizations(UserDB subject) {
    return OrganizationDB.findOrganizationsWhereUserIsOwnerOrMember(subject.id).list();
  }

  public PanacheQuery<OrganizationDB> getAllAccessibleOrganizationsPaged(UserDB subject, int index, int size) {
    return OrganizationDB.findOrganizationsWhereUserIsOwnerOrMember(subject.id).page(Page.of(index, size));
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

  public OrganizationDB updateOrganization(UserDB user, Long organizationId, UpdateOrganizationInput input) {
    final var organization = getOrThrow(organizationId);

    if (!userCanEditOrganization(user, organization)) {
      throw new RestException(Response.Status.FORBIDDEN, "Insufficient access rights.");
    }

    organization.name = input.name;
    organization.description = input.description;

    boolean logoAdded = organization.logo == null && input.logoId != null;
    boolean logoChanged = organization.logo != null && input.logoId != null && !organization.logo.id.equals(input.logoId);
    boolean logoRemoved = organization.logo != null && input.logoId == null;

    if (logoChanged || logoAdded) {
      if (organization.logo != null) {
        fileService.deleteFile(organization.logo);
      }

      organization.logo = (BaseFileDB) BaseFileDB.findByIdOptional(input.logoId)
              .orElseThrow(() -> new RestException(Response.Status.NOT_FOUND, "Logo file not found."));
    } else if (logoRemoved) {
      fileService.deleteFile(organization.logo);
      organization.logo = null;
    }

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
    return subject.isAdmin() || organization.owners.contains(subject);
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
