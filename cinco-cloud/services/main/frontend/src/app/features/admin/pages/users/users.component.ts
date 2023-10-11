import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal, NgbNav } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { User } from '../../../../core/models/user';
import { CreateUserModalComponent } from '../../components/create-user-modal/create-user-modal.component';
import { AddAdminModalComponent } from '../../components/add-admin-modal/add-admin-modal.component';
import { ModalUtilsService } from '../../../../core/services/utils/modal-utils.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Page } from '../../../../core/models/page';

@Component({
  selector: 'cc-users',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {

  @ViewChild('nav') nav: NgbNav;

  activeTab = 'users';
  usersPage: Page<User>;
  adminsPage: Page<User>;

  constructor(private userApi: UserApiService,
              private modalService: NgbModal,
              private modalUtils: ModalUtilsService,
              private toastService: ToastService,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      const tab = params['tab'] == null ? 'users' : params['tab'];
      this.activeTab = tab;
      this.handleTabChange({ nextId: tab });
    });
    this.loadUsers(0);
    this.loadAdmins(0);
  }

  loadUsers(page = 0): void {
    this.userApi.getAll(page, 25).subscribe({
      next: page => this.usersPage = page
    });
  }

  loadAdmins(page = 0): void {
    this.userApi.getAll(page, 25, "ADMIN").subscribe({
      next: page => this.adminsPage = page
    });
  }

  reloadPages(): void {
    this.loadUsers(this.usersPage.number);
    this.loadAdmins(this.adminsPage.number);
  }

  handleTabChange(tab: any): void {
    this.router.navigate(
      [],
      {
        relativeTo: this.activatedRoute,
        queryParams: { tab: tab.nextId },
        queryParamsHandling: 'merge'
      });
  }

  openCreateUserModal() {
    const ref = this.modalService.open(CreateUserModalComponent);
    ref.result
      .then(() => this.loadUsers(this.usersPage.number))
      .catch(() => {});
  }

  openAddAdminModal() {
    const ref = this.modalService.open(AddAdminModalComponent);
    ref.result
      .then(() => this.loadAdmins(this.adminsPage.number))
      .catch(() => {});
  }

  deleteUser(user: User) {
    this.modalUtils.confirm({
      text: 'Do you really want to delete this user?',
      confirmButtonText: 'Delete'
    }).then(() => {
      this.userApi.delete(user).subscribe({
        next: () => {
          this.reloadPages();
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: 'User has been deleted.'
          });
        },
        error: res => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `User could not be deleted. ${res.error.message}`
          });
        }
      });
    }).catch(() => {
    })
  }

  removeAdmin(admin: User) {
    this.modalUtils.confirm({
      text: 'Do you really want to remove admin rights from this user?',
      confirmButtonText: 'Yes'
    }).then(() => {
      this.userApi.removeAdminRole(admin).subscribe({
        next: () => {
          this.reloadPages();
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: 'Admin rights have been removed for the user.'
          });
        },
        error: res => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `Admin rights could not be removed. ${res.error.message}`
          });
        }
      });
    }).catch(() => {
    })
  }

  toggleUserStatus(user: User) {
    if (user.activated) {
      this.userApi.deactivate(user.id).subscribe({
        next: () => {
          this.reloadPages();
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: 'User has been deactivated.'
          });
        },
        error: res => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `Could not deactivate user. ${res.error.message}`
          });
        }
      })
    } else {
      this.userApi.activate(String(user.id), "").subscribe({
        next: () => {
          this.reloadPages();
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: 'User has been activated.'
          });
        },
        error: res => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `Could not activate user. ${res.error.message}`
          });
        }
      })
    }
  }

  get users() {
    return this.usersPage == null ? [] : this.usersPage.items;
  }

  get admins() {
    return this.adminsPage == null ? [] : this.adminsPage.items;
  }
}
