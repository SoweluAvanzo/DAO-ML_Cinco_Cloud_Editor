import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { faCogs, faFolderOpen, faUsers, faUserShield } from '@fortawesome/free-solid-svg-icons';
import { Organization } from '../../../../core/models/organization';
import { User } from '../../../../core/models/user';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';

@UntilDestroy()
@Component({
  selector: 'cc-organization',
  templateUrl: './organization.component.html'
})
export class OrganizationComponent implements OnInit {

  organization: Organization;
  user: User;

  icons = {
    cogs: faCogs,
    folderOpen: faFolderOpen,
    userShield: faUserShield,
    users: faUsers
  };

  constructor(private route: ActivatedRoute,
              private appStore: AppStoreService,
              public organizationStore: OrganizationStoreService) {
  }

  ngOnInit(): void {
    this.organization = this.route.snapshot.data['organization'];
    this.organizationStore.setOrganization(this.organization);
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: organization => this.organization = organization
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }
}
