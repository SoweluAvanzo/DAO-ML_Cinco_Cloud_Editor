import {Component} from '@angular/core';
import {AppStoreService} from '../../../../core/services/stores/app-store.service';
import {AuthApiService} from '../../../../core/services/api/auth-api.service';
import {User} from '../../../../core/models/user';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UpdateCurrentUserProfileInput} from '../../../../core/models/forms/update-current-user-profile-input';
import {UserApiService} from '../../../../core/services/api/user-api.service';
import {ToastService, ToastType} from '../../../../core/services/toast.service';
import {HttpClient} from "@angular/common/http";


@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html'
})
export class PersonalInformationComponent {

  private allowedFileTypes = ['image/jpeg', 'image/png'];

  public informationChangeForm: FormGroup = new FormGroup({
    'name': new FormControl('', [Validators.minLength(1)]),
    'email': new FormControl('', [Validators.email]),
    'picture': new FormControl('', []),
    'fileSource': new FormControl('', [])
  });

  constructor(private authApi: AuthApiService,
              private appStore: AppStoreService,
              private userApi: UserApiService,
              private toastService: ToastService,
              private client: HttpClient) {
  }

  public get currentUser(): User {
    return this.appStore.getUser();
  }

  onFileChange(event) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.informationChangeForm.patchValue({
        fileSource: file
      });
    }
  }

  changeInformation(): void {
    var update: UpdateCurrentUserProfileInput = new UpdateCurrentUserProfileInput();
    var currentUser: User = this.appStore.getUser();
    if (this.informationChangeForm.get('name').value) {
      update.name = this.informationChangeForm.get('name').value;
    } else {
      update.name = currentUser.name;
    }
    if (this.informationChangeForm.get('email').value) {
      update.email = this.informationChangeForm.get('email').value;
    } else {
      update.email = currentUser.email;
    }

    //TODO: schicke bild and http://cinco-cloud/api/files/create
    if (this.informationChangeForm.get('picture').value) {
      const file: File = this.informationChangeForm.get('fileSource').value;

      const formData = new FormData();
      formData.append('file', this.informationChangeForm.get('fileSource').value);

      if (this.allowedFileTypes.some(x => x === file.type)) {
        console.log("allowed image type")
        console.log(file.type)
        console.log(this.informationChangeForm.get('fileSource').value)

        let uploadPath = "http://cinco-cloud/api/files/create"

        this.client.post(uploadPath, formData)

          .subscribe(res => {

            console.log(res);

            alert('Uploaded Successfully.');

          })
        /*
        var content = JSON.stringify({a: 1, b: 'test'})
        const rawResponse = await fetch(myUrl, {
          method: 'POST',
          content: formData,
          headers: {'Content-Type': 'image/*'}
        });

        const response = await rawResponse.json();
        console.log(response);
        */
      } else {
        console.log("please upload a jpeg/png")
      }
    } else {
      console.log("no file")
    }

    //TODO: reihenfolge ändern sobald file upload funktioniert

    //skip wenn alle 3 form felder leer sind
    if ((!this.informationChangeForm.get('picture').value) &&
      (!this.informationChangeForm.get('name').value) &&
      (!this.informationChangeForm.get('email').value)) {

    } else {
      this.userApi.updateProfile(update).subscribe({
        next: updatedUser => {
          this.toastService.show({type: ToastType.SUCCESS, message: 'Your profile has been updated.'});
          this.appStore.setUser(updatedUser);
          this.informationChangeForm.reset({
            name: updatedUser.name,
            email: updatedUser.email
          });
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
}
