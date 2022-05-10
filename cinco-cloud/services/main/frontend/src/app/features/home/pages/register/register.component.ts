import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UserRegisterInput } from '../../../../core/models/forms/user-register-input';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {

  public registerForm: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(5)]),
    passwordConfirm: new FormControl('', [Validators.required, Validators.minLength(5)])
  });

  public constructor(private router: Router,
                     private userApi: UserApiService,
                     private settingsApi: SettingsApiService,
                     private toastService: ToastService) {
  }

  public ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => {
        if (!settings.allowPublicUserRegistration) {
          this.router.navigate(['/']);
        }
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not fetch application settings.`
        });
        console.error(res.data.message);
      }
    });
  }

  public register(): void {
    const input: UserRegisterInput = this.registerForm.value;
    this.userApi.register(input).subscribe({
      next: () => {
        this.router.navigate(['/login']);
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: 'Your account has been created.'
        });
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Your account could not be created. ${res.data.message}`
        });
      }
    });
  }
}
