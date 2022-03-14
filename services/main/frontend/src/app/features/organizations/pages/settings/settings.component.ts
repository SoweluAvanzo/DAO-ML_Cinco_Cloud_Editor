import { Component, OnInit } from '@angular/core';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Organization } from '../../../../core/models/organization';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { User } from '../../../../core/models/user';

@UntilDestroy()
@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  organization: Organization;
  user: User;

  constructor(public organizationStore: OrganizationStoreService,
              public appStore: AppStoreService) {
  }

  ngOnInit(): void {
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: organization => this.organization = organization
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }
}
