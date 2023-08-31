import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../../core/services/api/user-api.service';
import { User } from '../../../../../core/models/user';
import { Project } from '../../../../../core/models/project';
import { Organization } from '../../../../../core/models/organization';
import { ToastService, ToastType } from '../../../../../core/services/toast.service';
import { Router } from '@angular/router';
import { ProjectApiService } from '../../../../../core/services/api/project-api.service';
import { OrganizationApiService } from '../../../../../core/services/api/organization-api.service';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'cc-delete-user-modal',
  templateUrl: './delete-user-modal.component.html',
  styleUrls: ['./delete-user-modal.component.scss']
})
export class DeleteUserModalComponent implements OnInit {

  @Input()
  user: User;

  responsibleProjects: Project[];

  responsibleOrganizations: Organization[];

  soloProjects: Project[];

  admins: User[];

  confirmationForm = new UntypedFormGroup({
    'username': new UntypedFormControl('', Validators.required)
  })

  constructor(public modal: NgbActiveModal,
              private userApi: UserApiService,
              private router: Router,
              private toastService: ToastService,
              private projectApi: ProjectApiService,
              private organizationApi: OrganizationApiService) { }

  ngOnInit(): void {
    this.projectApi.getAll().subscribe({
      next: value => {
        this.responsibleProjects = value.filter(pro => pro.owner.username === this.user.username && pro.members.length > 0);
        this.soloProjects = value.filter(pro => pro.owner.username === this.user.username && pro.members.length == 0);
      }
    });

    this.organizationApi.getAll().subscribe({
      next: value => {
        this.responsibleOrganizations = value.filter(org => org.owners.find(own => own.username === this.user.username) != null && org.owners.length === 1);
      }
    });

    this.userApi.getAll().subscribe({
      next: users => this.admins = users.filter(user => user.isAdmin)
    });
  }

  deleteUser(){
    this.userApi.delete(this.user).subscribe({
      next: () => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: 'User has been deleted.'
        });
        this.modal.close();
        this.router.navigate(['/login']);
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `User could not be deleted. ${res.error.message}`
        });
      }
    });
  }

  canDelete(): boolean {
    return this.confirmationForm.valid
      && this.confirmationForm.value.username === this.user.username
      && this.responsibleProjects.length === 0
      && this.responsibleOrganizations.length === 0;
  }

  goToOrg(id: number){
    const navUrl = `/app/organizations/${id}/projects`;
    this.modal.close();
    this.router.navigate([navUrl]);
  }

  goToProject(id: number){
    const navUrl = `/app/projects/${id}`;
    this.modal.close();
    this.router.navigate([navUrl]);
  }

  goToAdmins(){
    this.modal.close();
    this.router.navigate(['/app/admin/users']);
  }
}
