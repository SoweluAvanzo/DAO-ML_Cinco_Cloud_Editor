import { Component, OnInit } from '@angular/core';
import { UIState, UiStoreService } from '../../services/stores/ui-store.service';
import { UntilDestroy } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'cc-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  ui: UIState;

  constructor(public uiStore: UiStoreService) {
  }

  ngOnInit(): void {
    this.uiStore.ui$.subscribe({
      next: ui => this.ui = ui
    });
  }

  get collapsedIcon() {
    return this.ui != null && this.ui.sidebarCollapsed ? 'angle-double-right' : 'angle-double-left';
  }
}
