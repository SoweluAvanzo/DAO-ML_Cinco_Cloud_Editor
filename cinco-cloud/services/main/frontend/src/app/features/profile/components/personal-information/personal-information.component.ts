import { Component, OnInit } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { User } from '../../../../core/models/user';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UpdateCurrentUserProfileInput } from '../../../../core/models/forms/update-current-user-profile-input';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { FileApiService, Upload } from '../../../../core/services/api/file-api.service';
import { FileReference } from '../../../../core/models/file-reference';

@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html',
  styleUrls: ['./personal-information.scss']
})
export class PersonalInformationComponent implements OnInit {

  private allowedFileTypes = ['image/jpeg', 'image/png'];

  informationChangeForm: FormGroup = new FormGroup({
    'name': new FormControl('', [Validators.required]),
    'email': new FormControl('', [Validators.required, Validators.email]),
    'picture': new FormControl('')
  });

  pictureReference: FileReference;

  constructor(private authApi: AuthApiService,
              private appStore: AppStoreService,
              private userApi: UserApiService,
              private toastService: ToastService,
              private fileApi: FileApiService) {
  }

  ngOnInit(): void {
    this.informationChangeForm.get('name').setValue(this.currentUser.name);
    this.informationChangeForm.get('email').setValue(this.currentUser.email);
    this.pictureReference = this.currentUser.profilePicture;
  }

  handleFileSelect(files: File[]): void {
    if (files.length > 0) {
      this.informationChangeForm.get('picture').setValue(files[0]);
    }
  }

  handleClear(): void {
    this.informationChangeForm.get('picture').setValue(null);
  }

  changeInformation(): void {
    let input = new UpdateCurrentUserProfileInput();
    input.name = this.informationChangeForm.get('name').value;
    input.email = this.informationChangeForm.get('email').value;

    if (this.informationChangeForm.get('picture').value) {
      const file: File = this.informationChangeForm.get('picture').value;

      if (this.allowedFileTypes.some(x => x === file.type)) {
        this.fileApi.upload(file).subscribe({
          next: (upload: Upload) => {
            if (upload.file != null) {
              input.profilePicture = upload.file;
              this.updateProfile(input);
            }
          },
          error: res => {
            this.toastService.show({
              type: ToastType.DANGER,
              message: `The file could not be uploaded.`
            });
          }
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
        if (updatedUser.email !== this.currentUser.email) {
          this.toastService.show({
            type: ToastType.SUCCESS,
            message: 'Your profile has been updated. Please login with your new credentials.'
          });
          this.appStore.logout().subscribe();
        } else {
          this.toastService.show({ type: ToastType.SUCCESS, message: 'Your profile has been updated.' });
          this.appStore.setUser(updatedUser);
          this.pictureReference = updatedUser.profilePicture;
        }
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `The profile could not be updated. ${res.error.message}`
        });
      }
    });
  }

  get profilePictureStyle(): any {
    return {
      backgroundImage: `url(${this.pictureReference.downloadPath})`,
      backgroundSize: 'cover',
      width: '100px',
      height: '100px'
    };
  }

  get currentUser(): User {
    return this.appStore.getUser();
  }
}
