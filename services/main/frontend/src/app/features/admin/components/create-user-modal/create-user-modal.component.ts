import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UserRegisterInput } from '../../../../core/models/forms/user-register-input';

@Component({
  selector: 'cc-create-user-modal',
  templateUrl: './create-user-modal.component.html',
  styleUrls: ['./create-user-modal.component.scss']
})
export class CreateUserModalComponent {

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(5)])
  });

  constructor(private userApi: UserApiService,
              public modal: NgbActiveModal) {
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
      error: console.error
    });
  }
}
