import {Component, Input, OnInit} from '@angular/core';
import {Project} from "../../../../../../core/models/project";

@Component({
  selector: 'cc-overview-widget',
  templateUrl: './overview-widget.component.html',
  styleUrls: ['./overview-widget.component.scss']
})
export class OverviewWidgetComponent {

  @Input()
  project: Project;

}
