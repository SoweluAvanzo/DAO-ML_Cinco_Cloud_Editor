import { Component, OnInit } from '@angular/core';
import { OrganizationStoreService } from '../../services/organization-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Organization } from '../../../../core/models/organization';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CreateProjectModalComponent } from '../../../internal/components/create-project-modal/create-project-modal.component';

@UntilDestroy()
@Component({
  selector: 'cc-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {

  organization: Organization;

  constructor(private organizationStore: OrganizationStoreService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.organizationStore.organization$.pipe(untilDestroyed(this)).subscribe({
      next: org => {
        this.organization = org;
        console.log(this.organization);
      }
    });
  }

  openCreateProjectModal(): void {
    const ref = this.modalService.open(CreateProjectModalComponent);
    ref.componentInstance.organization = this.organization;
    ref.result.then(project => {
      this.organization.projects.push(project);
    }).catch(() => {
    });
  }
}
