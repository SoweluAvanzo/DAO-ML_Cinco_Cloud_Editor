import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { GitInformationApiService } from '../../../../../../core/services/api/git-information-api.service';
import { fromJsog, toJsog } from '../../../../../../core/utils/jsog-utils';
import { GitInformation } from '../../../../../../core/models/git-information';

@Component({
  selector: 'cc-edit-git-information-card',
  templateUrl: './edit-git-information-card.component.html'
})
export class EditGitInformationCardComponent implements OnInit {

  @Input()
  project: Project;

  form: FormGroup;

  info: GitInformation;

  constructor(private gitInformationApi: GitInformationApiService) {
  }

  ngOnInit(): void {
    this.gitInformationApi.get(this.project.id).subscribe({
      next: info => {
        this.info = info;
        this.form = new FormGroup({
          'type': new FormControl(info.type, [Validators.required]),
          'repositoryUrl': new FormControl(info.repositoryUrl, [Validators.required]),
          'username': new FormControl(info.username, [Validators.required]),
          'password': new FormControl(info.password, [Validators.required]),
          'branch': new FormControl(info.branch),
          'genSubdirectory': new FormControl(info.genSubdirectory),
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
      next: updatedInfo => this.info = updatedInfo,
      error: console.error
    });
  }

}
