import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-logout',
  templateUrl: './logout.component.html'
})
export class LogoutComponent implements OnInit {

  constructor(private appStore: AppStoreService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.appStore.logout().subscribe({
      next: () => this.toastService.show({
        message: 'You have been logged out.',
        type: ToastType.INFO
      }),
      error: console.error
    });
  }
}
