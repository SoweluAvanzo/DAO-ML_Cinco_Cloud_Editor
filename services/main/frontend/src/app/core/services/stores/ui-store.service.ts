import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface UIState {
  sidebarCollapsed: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UiStoreService {

  private static readonly UI_KEY = 'cinco_cloud_ui';

  private ui = new BehaviorSubject<UIState>({
    sidebarCollapsed: false
  });

  constructor() {
    this.loadUIState();
  }

  get ui$(): Observable<UIState> {
    return this.ui.asObservable();
  }

  public collapseSidebar(): void {
    const ui = { ...this.ui.value, sidebarCollapsed: true };
    this.ui.next(ui);
    this.persistUIState();
  }

  public expandSidebar(): void {
    const ui = { ...this.ui.value, sidebarCollapsed: false };
    this.ui.next(ui);
    this.persistUIState();
  }

  public toggleSidebar(): void {
    if (this.ui.value.sidebarCollapsed) {
      this.expandSidebar();
    } else {
      this.collapseSidebar();
    }
  }

  private loadUIState(): void {
    const ui = localStorage.getItem(UiStoreService.UI_KEY);
    if (ui != null) {
      try {
        this.ui.next(JSON.parse(ui));
      } catch (e) {
        console.error(e);
      }
    }
  }

  private persistUIState(): void {
    localStorage.setItem(UiStoreService.UI_KEY, JSON.stringify(this.ui.value));
  }
}
