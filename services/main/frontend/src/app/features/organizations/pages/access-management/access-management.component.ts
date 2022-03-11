import { Component, OnInit } from '@angular/core';
import { Organization } from '../../../../core/models/organization';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { User } from '../../../../core/models/user';
import { OrganizationAccessRightVector } from '../../../../core/models/organization-access-right-vector';
import { OrganizationAccessRight } from '../../../../core/enums/organization-access-right';

@UntilDestroy()
@Component({
  selector: 'cc-access-management',
  templateUrl: './access-management.component.html',
  styleUrls: ['./access-management.component.css']
})
export class AccessManagementComponent implements OnInit {

  organization: Organization;
  accessRightVectors = new Map<number, OrganizationAccessRightVector>();

  constructor(public organizationStore: OrganizationStoreService) {
  }

  ngOnInit(): void {
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: org => this.organization = org
    });
    this.organizationStore.organizationAccessRights$.pipe(untilDestroyed(this)).subscribe({
      next: arvs => this.accessRightVectors = arvs
    });
  }

  getAssignableAccessRights(user: User): OrganizationAccessRight[] {
    const accessRights = [
      OrganizationAccessRight.CREATE_PROJECTS,
      OrganizationAccessRight.EDIT_PROJECTS,
      OrganizationAccessRight.DELETE_PROJECTS
    ];

    const accessRightsOfUser = this.accessRightVectors.get(user.id);
    return accessRightsOfUser == null || accessRightsOfUser.accessRights.length === 0
      ? accessRights
      : accessRights.filter(accessRight => !accessRightsOfUser.accessRights.includes(accessRight));
  }

  hasAllAccessRights(user: User): boolean {
    const accessRightsOfUser = this.accessRightVectors.get(user.id);
    return accessRightsOfUser != null && accessRightsOfUser.accessRights.length === 3;
  }
}
