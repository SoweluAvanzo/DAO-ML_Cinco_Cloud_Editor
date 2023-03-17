import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../../../../core/models/user';
import { faToggleOff, faToggleOn, faTrash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-user-list',
  templateUrl: './user-list.component.html'
})
export class UserListComponent {

  icons = {
    trash: faTrash,
    toggleOn: faToggleOn,
    toggleOff: faToggleOff
  };

  @Input()
  users: User[] = [];

  @Output()
  userDeletionRequest = new EventEmitter<any>();

  @Output()
  userToggleStatusRequest = new EventEmitter<any>();

  deleteUser(user: User) {
    this.userDeletionRequest.emit(user);
  }

  toogleUserStatus(user: User) {
    this.userToggleStatusRequest.emit(user);
  }
}
