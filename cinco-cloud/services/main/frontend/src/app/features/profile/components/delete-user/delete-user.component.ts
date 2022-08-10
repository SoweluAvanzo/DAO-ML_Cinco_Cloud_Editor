import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteUserModalComponent } from './delete-user-modal/delete-user-modal.component';
import { Project } from '../../../../core/models/project';
import { Organization } from '../../../../core/models/organization';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { OrganizationApiService } from '../../../../core/services/api/organization-api.service';
import { User } from '../../../../core/models/user';

@Component({
  selector: 'cc-delete-user',
  templateUrl: './delete-user.component.html'
})
export class DeleteUserComponent implements OnInit{

  user: User;
  admins: number;

  projects: Project[] = [];
  organizations: Organization[] = [];

  responsibleProjects: Project[] = [];
  soloProjects: Project [] = [];
  responsibleOrgs: Organization[] = [];

  constructor(private appStore: AppStoreService,
              private userApi: UserApiService,
              private modalService: NgbModal,
              private projectApi: ProjectApiService,
              private organizationApi: OrganizationApiService) { }

  ngOnInit() {
    this.projectApi.getAll().subscribe({
      next: value => this.projects = value
    })
    this.organizationApi.getAll().subscribe({
      next: value => this.organizations = value
    })
    this.appStore.user$.subscribe({
      next: value => this.user = value
    })
    this.userApi.getAll().subscribe({
      next: users => {
        this.admins = users.filter(user => user.isAdmin).length;
      },
      error: err => {
        this.admins = 0;
      }
    });
  }

  openDeleteModal(): void {
    this.responsibleProjects = this.projects.filter(pro => pro.owner.username === this.user.username && pro.members.length > 0);
    this.responsibleOrgs = this.organizations.filter(org => org.owners.find(own => own.username === this.user.username) != null && org.owners.length === 1);
    this.soloProjects = this.projects.filter(pro => pro.owner.username === this.user.username && pro.members.length == 0);
    const ref = this.modalService.open(DeleteUserModalComponent);
    ref.componentInstance.user = this.appStore.getUser();
    ref.componentInstance.responsibleProjects = this.responsibleProjects;
    ref.componentInstance.responsibleOrgs = this.responsibleOrgs;
    ref.componentInstance.soloProjects = this.soloProjects;
    ref.componentInstance.admins = this.admins;
  }
}
