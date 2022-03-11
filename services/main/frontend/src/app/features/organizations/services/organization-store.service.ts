import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Organization } from '../../../core/models/organization';
import { OrganizationApiService } from '../../../core/services/api/organization-api.service';
import { User } from '../../../core/models/user';

@Injectable()
export class OrganizationStoreService {

  private organization = new BehaviorSubject<Organization>(null);

  constructor(private organizationApi: OrganizationApiService) {
  }

  get organization$(): BehaviorSubject<Organization> {
    return this.organization;
  }

  setOrganization(organization: Organization): void {
    this.organization.next(organization);
  }

  removeUserFromOrganization(user: User): void {
    this.organizationApi.removeUser(this.organization.value, user).subscribe({
      next: organization => this.organization.next(organization),
      error: console.error
    });
  }

  makeUserMemberOfOrganization(user: User): void {
    this.organizationApi.addMember(this.organization.value, user).subscribe({
      next: organization => this.organization.next(organization),
      error: console.error
    });
  }

  makeUserOwnerOfOrganization(user: User): void {
    this.organizationApi.addOwner(this.organization.value, user).subscribe({
      next: organization => this.organization.next(organization),
      error: console.error
    });
  }
}
