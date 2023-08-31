import { Component } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { UpdateCurrentUserPasswordInput } from '../../../../core/models/forms/update-current-user-password-input';
import { Router } from '@angular/router';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-password-change',
  templateUrl: './password-change.component.html'
})
export class PasswordChangeComponent {

  public passwordChangeForm: UntypedFormGroup = new UntypedFormGroup({
    'password': new UntypedFormControl('', [Validators.required, Validators.minLength(5)]),
    'new_password': new UntypedFormControl('', [Validators.required, Validators.minLength(5)])
  })

  constructor(private authApi: AuthApiService,
              private appStore: AppStoreService,
              private userApi: UserApiService,
              private router: Router,
              private toastService: ToastService) {
  }

  public change_password(): void {
    const update: UpdateCurrentUserPasswordInput = new UpdateCurrentUserPasswordInput()
    update.oldPassword = this.passwordChangeForm.get('password').value
    update.newPassword = this.passwordChangeForm.get('new_password').value
    this.userApi.updatePassword(this.appStore.getUser(), update).subscribe({
      next: () => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: 'Your password has been changed. Please login again.'
        });
        this.router.navigate(['/logout']);
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `The password could not be changed. ${res.error.message}`
        });
      }
    });
  }

}
