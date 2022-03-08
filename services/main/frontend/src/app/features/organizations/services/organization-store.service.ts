import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Organization } from '../../../core/models/organization';

@Injectable()
export class OrganizationStoreService {

  private organization = new BehaviorSubject<Organization>(null);

  get organization$(): BehaviorSubject<Organization> {
    return this.organization;
  }

  setOrganization(organization: Organization) {
    this.organization.next(organization);
  }
}
