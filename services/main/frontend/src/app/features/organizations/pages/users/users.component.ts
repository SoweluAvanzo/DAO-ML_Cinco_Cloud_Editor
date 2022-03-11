import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { Organization } from '../../../../core/models/organization';
import { AddUserModalComponent } from './components/add-user-modal/add-user-modal.component';
import { User } from '../../../../core/models/user';

@UntilDestroy()
@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  organization: Organization;

  constructor(public organizationStore: OrganizationStoreService,
              private modalService: NgbModal,
              private organizationApi: OrganizationApiService) {
  }

  get users(): User[] {
    return this.organization == null ? [] : [...this.organization.owners, ...this.organization.members];
  }

  ngOnInit(): void {
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: org => this.organization = org
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

  isOrganizationOwner(user: User): boolean {
    return this.organization.owners.includes(user);
  }
}
