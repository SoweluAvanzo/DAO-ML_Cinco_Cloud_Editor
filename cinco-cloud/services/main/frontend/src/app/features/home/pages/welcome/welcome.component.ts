import { Component, OnInit } from '@angular/core';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { Settings } from '../../../../core/models/settings';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-welcome',
  templateUrl: './welcome.component.html'
})
export class WelcomeComponent implements OnInit {
  public settings: Settings;

  constructor(private settingsApi: SettingsApiService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => this.settings = settings,
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not fetch application settings.`
        });
        console.error(res);
      }
    });
  }
}
