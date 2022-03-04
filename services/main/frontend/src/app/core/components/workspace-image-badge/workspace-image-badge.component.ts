import { Component, Input, OnInit } from '@angular/core';
import { WorkspaceImage } from '../../models/workspace-image';

@Component({
  selector: 'cc-workspace-image-badge',
  templateUrl: './workspace-image-badge.component.html',
  styleUrls: ['./workspace-image-badge.component.css']
})
export class WorkspaceImageBadgeComponent {

  @Input()
  image: WorkspaceImage;
}
