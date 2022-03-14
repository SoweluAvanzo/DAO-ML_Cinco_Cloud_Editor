import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal, NgbNav } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { User } from '../../../../core/models/user';
import { CreateUserModalComponent } from '../../components/create-user-modal/create-user-modal.component';
import { AddAdminModalComponent } from '../../components/add-admin-modal/add-admin-modal.component';

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
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.users = users;
        this.admins = users.filter(user => user.isAdmin);
      }
    })
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
    this.userApi.delete(user).subscribe({
      //TODO: add notification
      next: () => {
        this.users.splice(this.users.findIndex(u => u.id === user.id), 1);
        if (user.isAdmin) {
          this.admins.splice(this.admins.findIndex(a => a.id === user.id), 1);
        }
      }
    })
  }

  removeAdmin(admin: User) {
    this.userApi.removeAdminRole(admin).subscribe({
      next: () => {
        //TODO: add notification
        this.admins.splice(this.admins.findIndex(a => a.id === admin.id), 1);
      }
    })
  }
}
