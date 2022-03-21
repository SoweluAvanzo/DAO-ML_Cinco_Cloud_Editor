import { Component, Input } from '@angular/core';
import { Project } from '../../../../../../core/models/project';

@Component({
  selector: 'cc-editor-widget',
  templateUrl: './editor-widget.component.html',
  styleUrls: ['./editor-widget.component.scss']
})
export class EditorWidgetComponent {

  @Input()
  project: Project;
}
