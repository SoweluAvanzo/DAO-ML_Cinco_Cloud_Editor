import { Component, OnInit } from '@angular/core';
import {AppStoreService} from "../../../../core/services/stores/app-store.service";
import { AuthApiService } from "../../../../core/services/api/auth-api.service";
import { SettingsApiService } from "../../../../core/services/api/settings-api.service";
import { Settings } from "../../../../core/models/settings";



@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html',
  styleUrls: ['./personal-information.component.css']
})
export class PersonalInformationComponent implements OnInit {

  public settings: Settings;

  constructor(private authApi: AuthApiService,
              private settingsApi: SettingsApiService,
              private appStore: AppStoreService) {

  }

  ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => this.settings = settings,
      error: console.error
    });
  }

}
