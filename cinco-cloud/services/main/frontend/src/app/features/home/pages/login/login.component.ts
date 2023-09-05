import { Component, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { Settings } from '../../../../core/models/settings';
import { UserLoginInput } from '../../../../core/models/forms/user-login-input';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { ActivatedRoute } from "@angular/router";
import { UserApiService } from "../../../../core/services/api/user-api.service";

@Component({
  selector: 'cc-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  public settings: Settings;

  public loginForm: UntypedFormGroup = new UntypedFormGroup({
    'emailOrUsername': new UntypedFormControl('', [Validators.required]),
    'password': new UntypedFormControl('', [Validators.required, Validators.minLength(5)])
  });

  constructor(private authApi: AuthApiService,
              private userApi: UserApiService,
              private settingsApi: SettingsApiService,
              private appStore: AppStoreService,
              private toastService: ToastService,
              private route: ActivatedRoute) {
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
    this.route.queryParams.subscribe(params => {
      const activationToken = params['token'];
      const userId = params['userId'];
      if (activationToken != null) {
        this.userApi.activate(userId, activationToken).subscribe({
          next: () => {
            this.toastService.show({
              type: ToastType.SUCCESS,
              message: `Your account has been activated. You can now login with your credentials.`
            });
          },
          error: res => {
            this.toastService.show({
              type: ToastType.DANGER,
              message: `There was a problem activating your user account. ${res.error.message}`
            });
          }
        });
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
