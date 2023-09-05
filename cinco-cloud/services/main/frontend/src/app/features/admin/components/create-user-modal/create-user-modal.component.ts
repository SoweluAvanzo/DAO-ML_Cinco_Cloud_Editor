import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { UserRegisterInput } from '../../../../core/models/forms/user-register-input';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@Component({
  selector: 'cc-create-user-modal',
  templateUrl: './create-user-modal.component.html'
})
export class CreateUserModalComponent {

  form = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required]),
    username: new UntypedFormControl('', [Validators.required]),
    email: new UntypedFormControl('', [Validators.required, Validators.email]),
    password: new UntypedFormControl('', [Validators.required, Validators.minLength(5)])
  });

  constructor(private userApi: UserApiService,
              public modal: NgbActiveModal,
              private toastService: ToastService) {
  }

  get canCreateUser(): boolean {
    return this.form.valid;
  }

  createUser(): void {
    const newUser = new UserRegisterInput();
    newUser.name = this.form.value.name;
    newUser.username = this.form.value.username;
    newUser.email = this.form.value.email;
    newUser.password = this.form.value.password;
    newUser.passwordConfirm = this.form.value.password;

    this.userApi.create(newUser).subscribe({
      next: createdUser => this.modal.close(createdUser),
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not create user.`
        });
        console.error(res.error.message);
      }
    });
  }
}
