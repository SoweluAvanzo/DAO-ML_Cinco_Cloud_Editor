import { Component, Input } from '@angular/core';
import { User } from '../../models/user';
import { Project } from '../../models/project';

@Component({
  selector: 'cc-project-role-badge',
  templateUrl: './project-role-badge.component.html'
})
export class ProjectRoleBadgeComponent {

  @Input()
  user: User;

  @Input()
  project: Project;

}
