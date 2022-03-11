import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { Organization } from '../../../../core/models/organization';
import { AddUserModalComponent } from './components/add-user-modal/add-user-modal.component';

@UntilDestroy()
@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  organization: Organization;

  constructor(public organizationStore: OrganizationStoreService,
              private modalService: NgbModal) {
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
}
