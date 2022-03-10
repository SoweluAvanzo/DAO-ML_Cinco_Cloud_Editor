import {Component, Input, OnInit} from '@angular/core';
import {AppStoreService} from "../../../../core/services/stores/app-store.service";
import { AuthApiService } from "../../../../core/services/api/auth-api.service";
import { SettingsApiService } from "../../../../core/services/api/settings-api.service";
import { Settings } from "../../../../core/models/settings";
import { UserProfileImageComponent } from "../../../../core/components/user-profile-image/user-profile-image.component";
import {User} from "../../../../core/models/user";
import {FormControl, FormGroup, Validators} from "@angular/forms";


@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html',
  styleUrls: ['./personal-information.component.css']
})
export class PersonalInformationComponent implements OnInit {

  public settings: Settings;

  public informationChangeForm: FormGroup = new FormGroup({
    'name': new FormControl('', [Validators.required, Validators.minLength(1)]),
    'email': new FormControl('', [Validators.required, Validators.email])
  })

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

  get currentUser(): User {
    return this.appStore.getUser()
  }

  changeInformation(): void {

  }

}
