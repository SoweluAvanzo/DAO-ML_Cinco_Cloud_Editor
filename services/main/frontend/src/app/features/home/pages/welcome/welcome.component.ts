import { Component, OnInit } from '@angular/core';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { Settings } from '../../../../core/models/settings';

@Component({
  selector: 'cc-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent implements OnInit {
  public settings: Settings;

  constructor(private settingsApi: SettingsApiService) {
  }

  ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => this.settings = settings,
      error: console.error
    });
  }
}
