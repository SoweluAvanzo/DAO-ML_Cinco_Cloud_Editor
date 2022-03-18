import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../../../../core/models/user';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-admin-list',
  templateUrl: './admin-list.component.html'
})
export class AdminListComponent {

  icons = {
    times: faTimes,
  };

  @Input()
  admins: User[] = [];

  @Output()
  removeAdminRoleRequest = new EventEmitter<any>();

  constructor() {
  }

  removeAdminRole(admin: User) {
    this.removeAdminRoleRequest.emit(admin);
  }
}
