import { Component, OnInit, ViewChild } from '@angular/core';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { combineLatest } from 'rxjs';
import { Project } from '../../../../core/models/project';
import { Organization } from '../../../../core/models/organization';
import { NgbModal, NgbNav } from '@ng-bootstrap/ng-bootstrap';
import { CreateProjectModalComponent } from '../../components/create-project-modal/create-project-modal.component';
import { CreateOrganizationModalComponent } from '../../components/create-organization-modal/create-organization-modal.component';

@Component({
  selector: 'cc-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit {

  @ViewChild('nav') nav: NgbNav;

  projects: Project[] = [];
  organizations: Organization[] = [];

  constructor(private projectApi: ProjectApiService,
              private organizationApi: OrganizationApiService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    combineLatest([
      this.projectApi.getAll(),
      this.organizationApi.getAll()
    ]).subscribe({
      next: (res: [Project[], Organization[]]) => {
        this.projects = res[0];
        this.organizations = res[1];
      }
    });
  }

  openCreateProjectModal(): void {
    const ref = this.modalService.open(CreateProjectModalComponent);
    ref.result.then(
      createdProject => this.projects.push(createdProject)
    ).catch(() => {
    });
  }

  openCreateOrganizationModal(): void {
    const ref = this.modalService.open(CreateOrganizationModalComponent);
    ref.result.then(
      createdOrganization => this.organizations.push(createdOrganization)
    ).catch(() => {
    });
  }
}
