import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { User } from '../../../../core/models/user';

@Component({
  selector: 'cc-add-admin-modal',
  templateUrl: './add-admin-modal.component.html',
  styleUrls: ['./add-admin-modal.component.scss']
})
export class AddAdminModalComponent {

  selectedUser: User;

  constructor(private userApi: UserApiService,
              public modal: NgbActiveModal) {
  }

  setSelectedUser(user: User) {
    this.selectedUser = user;
  }

  addAdminRole() {
    this.userApi.addAdminRole(this.selectedUser).subscribe({
      next: createdUser => this.modal.close(createdUser),
      error: console.error
    })
  }

  get canAddAdminRole(): boolean {
    return this.selectedUser != null && !this.selectedUser.isAdmin;
  }
}
