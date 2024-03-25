import { Component, OnInit } from '@angular/core';
import { Settings } from '../../../../core/models/settings';
import { SettingsApiService } from '../../../../core/services/api/settings-api.service';
import { faToggleOff, faToggleOn } from '@fortawesome/free-solid-svg-icons';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

  icons = {
    toggleOn: faToggleOn,
    toggleOff: faToggleOff
  };

  settings: Settings;

  settingsForm = new FormGroup({
    archetypeImage: new FormControl('', [Validators.required])
  })

  constructor(private settingsService: SettingsApiService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.settingsService.get().subscribe({
      next: settings => {
        this.settings = settings;
        this.settingsForm.controls.archetypeImage.setValue(settings.archetypeImage);
      }
    });
  }

  saveSettings() {
    console.log(this.settingsForm.value);
    this.settings.archetypeImage = this.settingsForm.value.archetypeImage;

    this.settingsService.update(this.settings).subscribe({
      next: settings => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: 'The settings have been updated.'
        });
        this.settings = settings;
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `The settings could not be updated. ${res.error.message}`
        });
      }
    });
  }

  setUserRegistration(allowPublicUserRegistration: boolean) {
    this.settings.allowPublicUserRegistration = allowPublicUserRegistration;
  }

  setAutoActivateUsers(autoActivateUsers: boolean) {
    this.settings.autoActivateUsers = autoActivateUsers;
  }

  setSendMails(sendMails: boolean) {
    this.settings.sendMails = sendMails;
  }

  get canSave(): boolean {
    return this.settingsForm.valid;
  }
}
