import { Component } from '@angular/core';
import { faCogs, faUsers, faTableColumns } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-admin',
  templateUrl: './admin.component.html'
})
export class AdminComponent {
  faCogs = faCogs;
  faUsers = faUsers;
  faTableColumns = faTableColumns;
}
