import { Component, OnInit, ViewChild } from '@angular/core';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { Project } from '../../../../core/models/project';
import { Organization } from '../../../../core/models/organization';
import { NgbModal, NgbNav } from '@ng-bootstrap/ng-bootstrap';
import { CreateProjectModalComponent } from '../../components/create-project-modal/create-project-modal.component';
import { CreateOrganizationModalComponent } from '../../components/create-organization-modal/create-organization-modal.component';
import { Page } from '../../../../core/models/page';

@Component({
  selector: 'cc-overview',
  templateUrl: './overview.component.html'
})
export class OverviewComponent implements OnInit {

  private readonly PAGE_SIZE = 15;

  @ViewChild('nav') nav: NgbNav;

  projectPage: Page<Project>;
  organizationPage: Page<Organization>;
  currentProjectPageIndex = 0;
  currentOrganizationPageIndex = 0;

  constructor(private projectApi: ProjectApiService,
              private organizationApi: OrganizationApiService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.loadProjectPage();
    this.loadOrganizationPage();
  }

  loadProjectPage(): void {
    this.projectApi.getAllPaged(this.currentProjectPageIndex, this.PAGE_SIZE).subscribe({
      next: projectsPage => this.projectPage = projectsPage
    })
  }

  nextProjectPage(): void {
    this.currentProjectPageIndex = Math.min(this.projectPage.amountOfPages, this.currentProjectPageIndex + 1);
    this.loadProjectPage();
  }

  previousProjectPage(): void {
    this.currentProjectPageIndex = Math.max(0, this.currentProjectPageIndex - 1);
    this.loadProjectPage();
  }

  loadOrganizationPage(): void {
    this.projectApi.getAllPaged(this.currentProjectPageIndex, this.PAGE_SIZE).subscribe({
      next: projectPage => this.projectPage = projectPage
    })

    this.organizationApi.getAllPaged(this.currentOrganizationPageIndex, this.PAGE_SIZE).subscribe({
      next: organizationPage => this.organizationPage = organizationPage
    })
  }

  nextOrganizationPage(): void {
    this.currentOrganizationPageIndex = Math.min(this.organizationPage.amountOfPages, this.currentOrganizationPageIndex + 1);
    this.loadOrganizationPage();
  }

  previousOrganizationPage(): void {
    this.currentOrganizationPageIndex = Math.max(0, this.currentOrganizationPageIndex - 1);
    this.loadOrganizationPage();
  }

  openCreateProjectModal(): void {
    const ref = this.modalService.open(CreateProjectModalComponent);
    ref.result.then(() => {
      this.loadProjectPage();
    }).catch(() => {
    });
  }

  openCreateOrganizationModal(): void {
    const ref = this.modalService.open(CreateOrganizationModalComponent);
    ref.result.then(() => {
      this.loadOrganizationPage();
    }).catch(() => {
    });
  }

  get projects(): Project[] {
    return this.projectPage == null ? [] : this.projectPage.items;
  }

  get organizations(): Organization[] {
    return this.organizationPage == null ? [] : this.organizationPage.items;
  }
}
