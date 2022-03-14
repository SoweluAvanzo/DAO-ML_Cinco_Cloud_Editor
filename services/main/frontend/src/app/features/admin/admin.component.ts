import { Component } from '@angular/core';
import { faCogs, faUsers } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-admin',
  templateUrl: './admin.component.html'
})
export class AdminComponent {

  icons = {
    users: faUsers,
    cogs: faCogs
  };
}
