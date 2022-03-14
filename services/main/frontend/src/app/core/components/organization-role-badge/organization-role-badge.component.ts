import { Component, Input } from '@angular/core';
import { User } from '../../models/user';
import { Organization } from '../../models/organization';

@Component({
  selector: 'cc-organization-role-badge',
  templateUrl: './organization-role-badge.component.html'
})
export class OrganizationRoleBadgeComponent {

  @Input()
  user: User;

  @Input()
  organization: Organization;
}
