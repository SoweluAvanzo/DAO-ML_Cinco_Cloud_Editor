import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Organization } from '../../../core/models/organization';
import { OrganizationApiService } from '../../../core/services/api/organization-api.service';
import { User } from '../../../core/models/user';
import { OrganizationAccessRightVectorApiService } from '../../../core/services/api/organization-access-right-vector-api.service';
import { OrganizationAccessRight } from '../../../core/enums/organization-access-right';
import { OrganizationAccessRightVector } from '../../../core/models/organization-access-right-vector';

@Injectable()
export class OrganizationStoreService {

  private organization = new BehaviorSubject<Organization>(null);
  private organizationAccessRights = new BehaviorSubject<Map<number, OrganizationAccessRightVector>>(new Map());

  constructor(private organizationApi: OrganizationApiService,
              private organizationARVApi: OrganizationAccessRightVectorApiService) {
  }

  get organization$(): Observable<Organization> {
    return this.organization.asObservable();
  }

  get organizationAccessRights$(): Observable<Map<number, OrganizationAccessRightVector>> {
    return this.organizationAccessRights.asObservable();
  }

  setOrganization(organization: Organization): void {
    this.organization.next(organization);
    this.organizationARVApi.getAll(organization.id).subscribe({
      next: accessRights => {
        const map = new Map<number, OrganizationAccessRightVector>();
        accessRights.forEach(arv => map.set(arv.user.id, arv));
        this.organizationAccessRights.next(map);
      }
    });
  }

  removeUserFromOrganization(user: User): void {
    this.organizationApi.removeUser(this.organization.value, user).subscribe({
      next: organization => {
        this.organization.next(organization);
        const arvMap = this.organizationAccessRights.value;
        arvMap.delete(user.id);
        this.organizationAccessRights.next(arvMap);
      },
      error: console.error
    });
  }

  makeUserMemberOfOrganization(user: User): void {
    this.organizationApi.addMember(this.organization.value, user).subscribe({
      next: organization => this.setOrganization(organization),
      error: console.error
    });
  }

  makeUserOwnerOfOrganization(user: User): void {
    this.organizationApi.addOwner(this.organization.value, user).subscribe({
      next: organization => this.setOrganization(organization),
      error: console.error
    });
  }

  addAccessRight(arv: OrganizationAccessRightVector, accessRight: OrganizationAccessRight): void {
    arv.accessRights.push(accessRight);
    this.organizationARVApi.update(arv).subscribe({
      next: updatedArv => {
        const arvMap = this.organizationAccessRights.value;
        arvMap.set(updatedArv.user.id, arv);
        this.organizationAccessRights.next(arvMap);
      }
    });
  }

  removeAccessRight(arv: OrganizationAccessRightVector, accessRight: OrganizationAccessRight): void {
    arv.accessRights = arv.accessRights.filter(ar => ar != accessRight);
    this.organizationARVApi.update(arv).subscribe({
      next: updatedArv => {
        const arvMap = this.organizationAccessRights.value;
        arvMap.set(updatedArv.user.id, arv);
        this.organizationAccessRights.next(arvMap);
      }
    });
  }
}
