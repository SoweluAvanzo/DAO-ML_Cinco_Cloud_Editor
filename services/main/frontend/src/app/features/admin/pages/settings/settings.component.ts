import { Component, OnInit } from '@angular/core';
import { Settings } from '../../../../core/models/settings';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { faToggleOff, faToggleOn } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

  icons = {
    toggleOn: faToggleOn,
    toggleOff: faToggleOff
  };

  settings: Settings;

  constructor(private settingsService: SettingsApiService) {
  }

  ngOnInit(): void {
    this.settingsService.get().subscribe({
      next: settings => {
        this.settings = settings;
      }
    });
  }

  saveSettings() {
    //TODO: add notification
    this.settingsService.update(this.settings).subscribe({
      next: settings => {
        this.settings = settings;
      }
    });
  }

  setUserRegistration(allowPublicUserRegistration: boolean) {
    this.settings.allowPublicUserRegistration = allowPublicUserRegistration;
  }
}
