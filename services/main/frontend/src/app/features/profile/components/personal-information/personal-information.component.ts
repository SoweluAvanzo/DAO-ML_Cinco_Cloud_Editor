import {Component, Input, OnInit} from '@angular/core';
import {AppStoreService} from "../../../../core/services/stores/app-store.service";
import {AuthApiService} from "../../../../core/services/api/auth-api.service";
import {SettingsApiService} from "../../../../core/services/api/settings-api.service";
import {Settings} from "../../../../core/models/settings";
import {UserProfileImageComponent} from "../../../../core/components/user-profile-image/user-profile-image.component";
import {User} from "../../../../core/models/user";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UpdateCurrentUserProfileInput} from "../../../../core/models/forms/update-current-user-profile-input";
import {UserApiService} from "../../../../core/services/api/user-api.service";
import {Router} from "@angular/router";


@Component({
  selector: 'cc-personal-information',
  templateUrl: './personal-information.component.html',
  styleUrls: ['./personal-information.component.css']
})
export class PersonalInformationComponent implements OnInit {

  public settings: Settings;
  private allowedFileTypes = ['image/jpeg', 'image/png']

  public informationChangeForm: FormGroup = new FormGroup({
    'name': new FormControl('', [Validators.minLength(1)]),
    'email': new FormControl('', [Validators.email]),
    'picture': new FormControl('', []),
    'fileSource': new FormControl('', [])
  })

  constructor(private authApi: AuthApiService,
              private settingsApi: SettingsApiService,
              private appStore: AppStoreService,
              private userApi: UserApiService,
              private router: Router) {

  }

  public get currentUser(): User{
    return this.appStore.getUser();
  }

  ngOnInit(): void {
    this.settingsApi.get().subscribe({
      next: settings => this.settings = settings,
      error: console.error
    });
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
    var update: UpdateCurrentUserProfileInput = new UpdateCurrentUserProfileInput()
    var currentUser: User = this.appStore.getUser()
    if (this.informationChangeForm.get('name').value) {
      update.name = this.informationChangeForm.get('name').value
    } else {
      update.name = currentUser.name
    }
    if (this.informationChangeForm.get('email').value) {
      update.email = this.informationChangeForm.get('email').value
    } else {
      update.email = currentUser.email
    }

    if (this.informationChangeForm.get('picture').value) {
      var file: File = this.informationChangeForm.get('fileSource').value

      if(this.allowedFileTypes.some(x => x === file.type)){
        console.log("allowed image type")
        console.log(file.type)
        console.log(this.informationChangeForm.get('fileSource').value)
      } else {
        console.log("please upload a jpeg/png")
      }
    } else {
      console.log("no file")
    }

    this.userApi.updateProfile(update).subscribe({
      next: () => this.router.navigate(['/app/overview']),
      error: console.error
    })
  }

}
