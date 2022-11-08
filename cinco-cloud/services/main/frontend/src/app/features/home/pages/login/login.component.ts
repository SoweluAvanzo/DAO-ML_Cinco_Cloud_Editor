import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { Settings } from '../../../../core/models/settings';
import { UserLoginInput } from '../../../../core/models/forms/user-login-input';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  public settings: Settings;

  public loginForm: FormGroup = new FormGroup({
    'emailOrUsername': new FormControl('', [Validators.required]),
    'password': new FormControl('', [Validators.required, Validators.minLength(5)])
  });

  constructor(private authApi: AuthApiService,
              private settingsApi: SettingsApiService,
              private appStore: AppStoreService,
              private toastService: ToastService) {
  }

  public get canRegister(): boolean {
    return this.settings && this.settings.allowPublicUserRegistration;
  }

  public ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => this.settings = settings,
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not fetch application settings.`
        });
        console.error(res.error.message);
      }
    });
  }

  public login(): void {
    const input: UserLoginInput = this.loginForm.value;
    this.appStore.login(input).subscribe({
      next: () => {
        this.toastService.show({
          message: `Hello ${this.appStore.getUser().name}!`,
          type: ToastType.SUCCESS
        });
      },
      error: res => {
        this.toastService.show({type: ToastType.DANGER, message: `Login denied.\n ${res.error.message}`});
      }
    });
  }
}
