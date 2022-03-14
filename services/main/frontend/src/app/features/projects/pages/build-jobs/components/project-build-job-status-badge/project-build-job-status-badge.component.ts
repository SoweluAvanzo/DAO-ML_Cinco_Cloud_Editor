import { Component, Input } from '@angular/core';
import { WorkspaceImageBuildJob } from '../../../../../../core/models/workspace-image-build-job';

@Component({
  selector: 'cc-project-build-job-status-badge',
  templateUrl: './project-build-job-status-badge.component.html',
  styleUrls: ['./project-build-job-status-badge.component.css']
})
export class ProjectBuildJobStatusBadgeComponent {

  @Input()
  job: WorkspaceImageBuildJob;
}
