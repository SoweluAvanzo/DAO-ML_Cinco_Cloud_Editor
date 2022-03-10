import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {AppStoreService} from "../../../../core/services/stores/app-store.service";
import { AuthApiService } from "../../../../core/services/api/auth-api.service";
import { SettingsApiService } from "../../../../core/services/api/settings-api.service";
import { Settings } from "../../../../core/models/settings";
import {UserLoginInput} from "../../../../core/models/forms/user-login-input";
import {UserApiService} from "../../../../core/services/api/user-api.service";
import {User} from "../../../../core/models/user";
import {UpdateCurrentUserPasswordInput} from "../../../../core/models/forms/update-current-user-password-input";
import { Router } from '@angular/router';

@Component({
  selector: 'cc-password-change',
  templateUrl: './password-change.component.html',
  styleUrls: ['./password-change.component.css']
})
export class PasswordChangeComponent implements OnInit {

  public settings: Settings;

  public passwordChangeForm: FormGroup = new FormGroup({
    'password': new FormControl('', [Validators.required, Validators.minLength(5)]),
    'new_password': new FormControl('', [Validators.required, Validators.minLength(5)])
  })

  constructor(private authApi: AuthApiService,
              private settingsApi: SettingsApiService,
              private appStore: AppStoreService,
              private userApi: UserApiService,
              private router: Router) {

  }

  ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => this.settings = settings,
      error: console.error
    });
  }

  public change_password(): void{
    const update: UpdateCurrentUserPasswordInput = new UpdateCurrentUserPasswordInput()
    update.oldPassword = this.passwordChangeForm.get('password').value
    update.newPassword = this.passwordChangeForm.get('new_password').value
    this.userApi.updatePassword(update).subscribe({
      next: () => this.router.navigate(['/app/overview']),
      error: console.error
    });
  }

}
