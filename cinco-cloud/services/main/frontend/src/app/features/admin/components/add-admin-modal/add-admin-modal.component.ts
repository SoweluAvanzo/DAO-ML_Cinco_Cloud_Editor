import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { User } from '../../../../core/models/user';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-add-admin-modal',
  templateUrl: './add-admin-modal.component.html'
})
export class AddAdminModalComponent {

  selectedUser: User;

  constructor(private userApi: UserApiService,
              public modal: NgbActiveModal,
              private toastService: ToastService) {
  }

  get canAddAdminRole(): boolean {
    return this.selectedUser != null && !this.selectedUser.isAdmin;
  }

  setSelectedUser(user: User) {
    this.selectedUser = user;
  }

  addAdminRole() {
    this.userApi.addAdminRole(this.selectedUser).subscribe({
      next: createdUser => this.modal.close(createdUser),
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not promote user to admin.`
        });
        console.error(res.data.message);
      }
    });
  }
}
