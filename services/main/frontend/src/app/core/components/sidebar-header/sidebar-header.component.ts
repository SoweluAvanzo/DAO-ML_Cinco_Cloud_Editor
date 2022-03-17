import { Component, Input } from '@angular/core';
import { FileReference } from '../../models/file-reference';

@Component({
  selector: 'cc-sidebar-header',
  templateUrl: './sidebar-header.component.html',
  styleUrls: ['./sidebar-header.component.scss']
})
export class SidebarHeaderComponent {

  @Input()
  image: FileReference;

  @Input()
  text: string;

  @Input()
  subText: string;

  get style(): any {
    return this.image == null ? {} : {
      backgroundImage: `url(${this.image.downloadPath})`
    };
  }
}
