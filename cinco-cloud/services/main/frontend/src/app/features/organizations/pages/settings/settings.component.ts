import { Component, OnInit } from '@angular/core';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Organization } from '../../../../core/models/organization';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { User } from '../../../../core/models/user';
import { OrganizationApiService } from "../../../../core/services/api/organization-api.service";

@UntilDestroy()
@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

  organization: Organization;
  user: User;
  hasActiveBuildJobs: boolean;

  constructor(public organizationStore: OrganizationStoreService,
              public organizationApi: OrganizationApiService,
              public appStore: AppStoreService) {
  }

  ngOnInit(): void {
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: organization => {
        this.organization = organization;
        this.organizationApi.hasActiveBuildJobs(organization).subscribe({
          next: res => this.hasActiveBuildJobs = res.value
        })
      }
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }
}
