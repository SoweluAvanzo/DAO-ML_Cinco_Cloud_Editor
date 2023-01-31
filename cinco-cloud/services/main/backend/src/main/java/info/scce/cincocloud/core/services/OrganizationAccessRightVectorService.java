package info.scce.cincocloud.core.services;

import info.scce.cincocloud.db.OrganizationAccessRight;
import info.scce.cincocloud.db.OrganizationAccessRightVectorDB;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.UserDB;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class OrganizationAccessRightVectorService {

  public OrganizationAccessRightVectorDB create(UserDB user, OrganizationDB organization) {
    final OrganizationAccessRightVectorDB arv = new OrganizationAccessRightVectorDB();
    arv.user = user;
    arv.organization = organization;
    arv.persist();

    return arv;
  }

  public OrganizationAccessRightVectorDB getOrThrow(long organizationAccessRightVectorId) {
    return (OrganizationAccessRightVectorDB) OrganizationAccessRightVectorDB.findByIdOptional(organizationAccessRightVectorId)
        .orElseThrow(() -> new EntityNotFoundException("Cannot find access-right-vector."));
  }

  public OrganizationAccessRightVectorDB getByUserAndOrganization(UserDB subject, OrganizationDB org) {
    return OrganizationAccessRightVectorDB.findOrganizationAccessRightsForUser(subject, org).firstResult();
  }

  public Optional<OrganizationAccessRightVectorDB> getByUserAndOrganizationOptional(UserDB subject, OrganizationDB organization) {
    return Optional.ofNullable(getByUserAndOrganization(subject, organization));
  }

  public Collection<OrganizationAccessRightVectorDB> getAll(OrganizationDB organization) {
    return OrganizationAccessRightVectorDB.listAll().stream()
        .map(v -> (OrganizationAccessRightVectorDB) v)
        .collect(Collectors.toList());
  }

  public void deleteByUserAndOrganization(UserDB user, OrganizationDB org) {
    delete(getByUserAndOrganization(user, org));
  }

  public void delete(OrganizationAccessRightVectorDB organizationAccessRightVector) {
    organizationAccessRightVector.delete();
  }

  public void removeOwnerRights(OrganizationAccessRightVectorDB organizationAccessRightVector) {
    List.of(OrganizationAccessRight.CREATE_PROJECTS, OrganizationAccessRight.EDIT_PROJECTS,
        OrganizationAccessRight.DELETE_PROJECTS).forEach(organizationAccessRightVector.accessRights::remove);
  }

  public void addOwnerRights(OrganizationAccessRightVectorDB organizationAccessRightVector) {
    List.of(OrganizationAccessRight.CREATE_PROJECTS, OrganizationAccessRight.EDIT_PROJECTS,
        OrganizationAccessRight.DELETE_PROJECTS).forEach(organizationAccessRightVector.accessRights::add);
  }

  public void setAccessRights(OrganizationAccessRightVectorDB organizationAccessRightVectorDB, Set<OrganizationAccessRight> accessRights) {
    organizationAccessRightVectorDB.accessRights.clear();
    organizationAccessRightVectorDB.accessRights.addAll(accessRights);
    organizationAccessRightVectorDB.persist();
  }
}
