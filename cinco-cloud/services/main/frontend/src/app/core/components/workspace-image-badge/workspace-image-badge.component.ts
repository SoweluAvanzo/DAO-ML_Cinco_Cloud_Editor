import { Component, Input } from '@angular/core';
import { WorkspaceImage } from '../../models/workspace-image';

@Component({
  selector: 'cc-workspace-image-badge',
  templateUrl: './workspace-image-badge.component.html',
  styleUrls: ['./workspace-image-badge.component.scss']
})
export class WorkspaceImageBadgeComponent {

  @Input()
  image: WorkspaceImage;
}
