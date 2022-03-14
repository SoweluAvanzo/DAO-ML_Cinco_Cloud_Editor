import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal, NgbNav } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { User } from '../../../../core/models/user';
import { CreateUserModalComponent } from '../../components/create-user-modal/create-user-modal.component';
import { AddAdminModalComponent } from '../../components/add-admin-modal/add-admin-modal.component';
import { ModalUtilsService } from '../../../../core/services/utils/modal-utils.service';

@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  @ViewChild('nav') nav: NgbNav;

  users: User[] = [];
  admins: User[] = [];

  constructor(private userApi: UserApiService,
              private modalService: NgbModal,
              private modalUtils: ModalUtilsService) {
  }

  ngOnInit(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.users = users;
        this.admins = users.filter(user => user.isAdmin);
      }
    });
  }

  openCreateUserModal() {
    const ref = this.modalService.open(CreateUserModalComponent);
    ref.result.then(
      createdUser => this.users.push(createdUser)
    ).catch(() => {
    });
  }

  openAddAdminModal() {
    const ref = this.modalService.open(AddAdminModalComponent);
    ref.result.then(
      addedAdmin => this.admins.push(addedAdmin)
    ).catch(() => {
    });
  }

  deleteUser(user: User) {
    this.modalUtils.confirm({
      text: 'Do you really want to delete this user?',
      confirmButtonText: 'Delete'
    }).then(() => {
      this.userApi.delete(user).subscribe({
        //TODO: add notification
        next: () => {
          this.users.splice(this.users.findIndex(u => u.id === user.id), 1);
          if (user.isAdmin) {
            this.admins.splice(this.admins.findIndex(a => a.id === user.id), 1);
          }
        }
      });
    }).catch(() => {})
  }

  removeAdmin(admin: User) {
    this.modalUtils.confirm({
      text: 'Do you really want to remove admin rights from this user?',
      confirmButtonText: 'Yes'
    }).then(() => {
      this.userApi.removeAdminRole(admin).subscribe({
        next: () => {
          //TODO: add notification
          this.admins.splice(this.admins.findIndex(a => a.id === admin.id), 1);
        }
      });
    }).catch(() => {})
  }
}
