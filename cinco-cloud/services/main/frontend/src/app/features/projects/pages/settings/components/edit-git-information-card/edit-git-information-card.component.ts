import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { GitInformationApiService } from '../../../../../../core/services/api/git-information-api.service';
import { fromJsog, toJsog } from '../../../../../../core/utils/jsog-utils';
import { GitInformation } from '../../../../../../core/models/git-information';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';

@Component({
  selector: 'cc-edit-git-information-card',
  templateUrl: './edit-git-information-card.component.html'
})
export class EditGitInformationCardComponent implements OnInit {

  @Input()
  project: Project;

  form: UntypedFormGroup;

  info: GitInformation;

  constructor(private gitInformationApi: GitInformationApiService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.gitInformationApi.get(this.project.id).subscribe({
      next: info => {
        this.info = info;
        this.form = new UntypedFormGroup({
          'type': new UntypedFormControl(info.type, [Validators.required]),
          'repositoryUrl': new UntypedFormControl(info.repositoryUrl, [Validators.required]),
          'username': new UntypedFormControl(info.username, [Validators.required]),
          'password': new UntypedFormControl(info.password, [Validators.required]),
          'branch': new UntypedFormControl(info.branch),
          'genSubdirectory': new UntypedFormControl(info.genSubdirectory),
        });
      }
    });
  }

  update(): void {
    const form: GitInformation = this.form.value;
    const copy: GitInformation = fromJsog(toJsog(this.info), GitInformation);
    copy.type = form.type;
    copy.repositoryUrl = form.repositoryUrl;
    copy.username = form.username;
    copy.password = form.password;
    copy.branch = form.branch;
    copy.genSubdirectory = form.genSubdirectory;
    this.gitInformationApi.update(copy).subscribe({
      next: updatedInfo => {
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: 'The information have been updated.'
        });
        this.info = updatedInfo;
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `The information could not be updated. ${res.error.message}`
        });
      }
    });
  }

  get canSave(): boolean {
    return this.form.get('type').value === 'NONE' || (this.form.get('type').value === 'BASIC' && this.form.valid);
  }
}
