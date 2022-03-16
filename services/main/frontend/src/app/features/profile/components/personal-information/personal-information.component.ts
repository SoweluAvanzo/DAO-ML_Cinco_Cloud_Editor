import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { User } from '../../../../core/models/user';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UpdateCurrentUserProfileInput } from '../../../../core/models/forms/update-current-user-profile-input';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { FileApiService, Upload } from '../../../../core/services/api/file-api.service';

@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html'
})
export class PersonalInformationComponent implements OnInit {

  private allowedFileTypes = ['image/jpeg', 'image/png'];

  public informationChangeForm: FormGroup = new FormGroup({
    'name': new FormControl('', [Validators.required]),
    'email': new FormControl('', [Validators.required, Validators.email]),
    'picture': new FormControl('')
  });

  constructor(private authApi: AuthApiService,
              private appStore: AppStoreService,
              private userApi: UserApiService,
              private toastService: ToastService,
              private fileApi: FileApiService) {
  }

  ngOnInit(): void {
    this.informationChangeForm.get('name').setValue(this.currentUser.name);
    this.informationChangeForm.get('email').setValue(this.currentUser.email);
  }

  public get currentUser(): User {
    return this.appStore.getUser();
  }

  onFileChange(event) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.informationChangeForm.get('picture').setValue(file);
    }
  }

  changeInformation(): void {
    let input = new UpdateCurrentUserProfileInput();
    input.name = this.informationChangeForm.get('name').value;
    input.email = this.informationChangeForm.get('email').value;

    if (this.informationChangeForm.get('picture').value) {
      const file: File = this.informationChangeForm.get('picture').value;

      if (this.allowedFileTypes.some(x => x === file.type)) {
        this.fileApi.create(file).subscribe({
          next: (upload: Upload) => {
            if (upload.file != null) {
              input.profilePicture = upload.file;
              this.updateProfile(input);
            }
          },
          error: () => this.toastService.show({
            type: ToastType.DANGER,
            message: 'The file could not be uploaded.'
          })
        });
      } else {
        this.toastService.show({
          type: ToastType.INFO,
          message: 'Please upload a jpeg/png'
        });
      }
    } else {
      this.updateProfile(input);
    }
  }

  private updateProfile(input: UpdateCurrentUserProfileInput): void {
    this.userApi.updateProfile(input).subscribe({
      next: updatedUser => {
        this.toastService.show({ type: ToastType.SUCCESS, message: 'Your profile has been updated.' });
        this.appStore.setUser(updatedUser);
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `The profile could not be updated. ${res.data.message}`
        });
      }
    });
  }
}
