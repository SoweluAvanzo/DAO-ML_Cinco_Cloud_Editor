import { Component, Input } from '@angular/core';
import { IconDefinition } from '@fortawesome/free-regular-svg-icons';

@Component({
  selector: 'cc-sidebar-item',
  templateUrl: './sidebar-item.component.html',
  styleUrls: ['./sidebar-item.component.scss']
})
export class SidebarItemComponent {

  @Input()
  text: string;

  @Input()
  icon: IconDefinition;

  @Input()
  route: string[];

  @Input()
  routerLinkActiveExact: boolean = true;

  @Input()
  state: {[p: string]: any} = {};
}
