package info.scce.cincocloud.core;

import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class OrganizationService {
  @Inject
  ProjectService projectService;

  public void deleteOrganization(OrganizationDB organization) {
    deleteAllProjects(organization);
    deleteAccessRightVectors(organization);

    organization.members.clear();
    organization.owners.clear();
    organization.projects.clear();
    organization.delete();
  }

  private void deleteAllProjects(OrganizationDB org) {
    Iterator<ProjectDB> iter = org.projects.iterator();
    while (iter.hasNext()) {
      ProjectDB project = iter.next();
      projectService.deleteById(project.id);
      iter = org.projects.iterator();
    }
  }

  private void deleteAccessRightVectors(OrganizationDB org) {
    List<OrganizationAccessRightVectorDB> accessRightVectors = findAccessRightVectors(org);
    for (OrganizationAccessRightVectorDB vector : accessRightVectors) {
      vector.accessRights.clear();
      vector.organization = null;
      vector.user = null;
      vector.persist();
      vector.delete();
    }
  }

  private List<OrganizationAccessRightVectorDB> findAccessRightVectors(OrganizationDB org) {
    return OrganizationAccessRightVectorDB.list("organization = ?1", org);
  }
}
