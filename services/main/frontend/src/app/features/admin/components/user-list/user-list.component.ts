import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../../../../core/models/user';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { UserApiService } from '../../../../core/services/api/user-api.service';

@Component({
  selector: 'cc-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent {

  icons = {
    trash: faTrash,
  }

  @Input()
  users: User[] = [];

  @Output()
  onUserDeletionRequest = new EventEmitter<any>();

  constructor() {
  }

  deleteUser(user: User) {
    this.onUserDeletionRequest.emit(user);
  }
}
