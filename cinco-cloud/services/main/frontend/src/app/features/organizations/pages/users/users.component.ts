import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { Organization } from '../../../../core/models/organization';
import { AddUserModalComponent } from './components/add-user-modal/add-user-modal.component';
import { User } from '../../../../core/models/user';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';

@UntilDestroy()
@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {

  organization: Organization;
  currentUser: User;

  constructor(public organizationStore: OrganizationStoreService,
              private appStore: AppStoreService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: org => this.organization = org
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.currentUser = user
    });
  }

  openSearchUserModal(): void {
    const ref = this.modalService.open(AddUserModalComponent);
    ref.componentInstance.organization = this.organization;
    ref.result
      .then(organization => this.organizationStore.setOrganization(organization))
      .catch(() => {
      });
  }
}
