import { Component, OnInit, ViewChild } from '@angular/core';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { User } from '../../../../core/models/user';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { UpdateCurrentUserProfileInput } from '../../../../core/models/forms/update-current-user-profile-input';
import { UserApiService } from '../../../../core/services/api/user-api.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { FileApiService, Upload } from '../../../../core/services/api/file-api.service';
import { FileReference } from '../../../../core/models/file-reference';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { FileInputComponent } from '../../../../core/components/file-input/file-input.component';
import {
  UpdateCurrentUserProfilePictureInput
} from "../../../../core/models/forms/update-current-user-profile-picture-input";

@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html',
  styleUrls: ['./personal-information.scss']
})
export class PersonalInformationComponent implements OnInit {
  faTrash = faTrash;

  @ViewChild("input")
  input: FileInputComponent;

  private allowedFileTypes = ['image/jpeg', 'image/png'];

  informationChangeForm: UntypedFormGroup = new UntypedFormGroup({
    'name': new UntypedFormControl('', [Validators.required]),
    'email': new UntypedFormControl('', [Validators.required, Validators.email])
  });

  pictureReference: FileReference;

  constructor(private appStore: AppStoreService,
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
      const file = files[0];
      if (this.allowedFileTypes.some(x => x === file.type)) {
        this.fileApi.upload(file).subscribe({
          next: (upload: Upload) => {
            if (upload.file != null) {
              let input = new UpdateCurrentUserProfilePictureInput();
              input.profilePicture = upload.file;
              this.updateProfilePicture(input);
              this.input.reset();
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
    }
  }

  removeAvatar(): void {
    let input = new UpdateCurrentUserProfilePictureInput();
    input.profilePicture = null;
    this.updateProfilePicture(input);
  }

  changeInformation(): void {
    let input = new UpdateCurrentUserProfileInput();
    input.name = this.informationChangeForm.get('name').value;
    input.email = this.informationChangeForm.get('email').value;
    this.updateProfile(input);
  }

  private updateProfile(input: UpdateCurrentUserProfileInput): void {
    this.userApi.updateProfile(this.currentUser, input).subscribe({
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

  private updateProfilePicture(input: UpdateCurrentUserProfilePictureInput): void {
    this.userApi.updateProfilePicture(this.currentUser, input).subscribe({
      next: updatedUser => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: 'Your profile picture has been updated.'
        });
        this.appStore.setUser(updatedUser);
        this.pictureReference = updatedUser.profilePicture;
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `The profile picture could not be updated. ${res.error.message}`
        });
      }
    });
  }

  get profilePictureStyle(): any {
    const imageUrl = this.currentUser.profilePicture == null ?
      `https://www.gravatar.com/avatar/${this.currentUser.emailHash}?d=retro`
      : this.pictureReference.downloadPath;

    return {
      backgroundImage: `url(${imageUrl})`,
      backgroundSize: 'cover',
      width: '100px',
      height: '100px'
    };
  }

  get currentUser(): User {
    return this.appStore.getUser();
  }
}
