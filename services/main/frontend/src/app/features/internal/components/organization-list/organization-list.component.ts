import { Component, Input } from '@angular/core';
import { Organization } from '../../../../core/models/organization';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { User } from '../../../../core/models/user';

@UntilDestroy()
@Component({
  selector: 'cc-organization-list',
  templateUrl: './organization-list.component.html',
  styleUrls: ['./organization-list.component.scss']
})
export class OrganizationListComponent {

  @Input()
  organizations: Organization[] = [];

  currentUser: User;

  constructor(private appStore: AppStoreService) {
    appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.currentUser = user
    });
  }

  getOrganizationBackgroundImageStyle(organization: Organization): string {
    return organization.logo != null
      ? `background-image: url(${organization.logo.downloadPath}); background-size: cover`
      : '';
  }

  isUserOwnerOfOrganization(organization: Organization): boolean {
    return organization.owners.findIndex(o => o.id === this.currentUser.id) > -1;
  }
}
