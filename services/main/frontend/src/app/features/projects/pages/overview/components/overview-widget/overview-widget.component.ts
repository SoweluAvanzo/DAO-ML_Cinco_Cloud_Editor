import { Component, Input } from '@angular/core';
import { Project } from '../../../../../../core/models/project';

@Component({
  selector: 'cc-overview-widget',
  templateUrl: './overview-widget.component.html'
})
export class OverviewWidgetComponent {

  @Input()
  project: Project;
}
