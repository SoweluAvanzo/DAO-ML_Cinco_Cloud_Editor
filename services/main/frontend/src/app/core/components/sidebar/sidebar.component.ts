import { Component } from '@angular/core';

@Component({
  selector: 'cc-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {

  collapsed: boolean = false

  get collapsedIcon() {
    return this.collapsed ? 'angle-double-right' : 'angle-double-left';
  }
}
