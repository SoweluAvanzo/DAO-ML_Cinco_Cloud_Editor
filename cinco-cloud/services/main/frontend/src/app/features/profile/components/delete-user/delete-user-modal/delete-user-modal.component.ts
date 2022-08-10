import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../../core/services/api/user-api.service';
import { User } from '../../../../../core/models/user';
import { Project } from '../../../../../core/models/project';
import { Organization } from '../../../../../core/models/organization';
import { ToastService, ToastType } from '../../../../../core/services/toast.service';
import { Router } from '@angular/router';

@Component({
  selector: 'cc-delete-user-modal',
  templateUrl: './delete-user-modal.component.html',
  styleUrls: ['./delete-user-modal.component.scss']
})
export class DeleteUserModalComponent {

  @Input()
  user: User;

  @Input()
  responsibleProjects: Project[];

  @Input()
  responsibleOrgs: Organization[];

  @Input()
  soloProjects: Project[];

  @Input()
  admins: number;

  userName: String;

  constructor(public modal: NgbActiveModal,
              private userApi: UserApiService,
              private router: Router,
              private toastService: ToastService) { }

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
