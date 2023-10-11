import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { ProjectStoreService } from '../../../../services/project-store.service';
import { UpdateProjectInput } from '../../../../../../core/models/forms/update-project-input';
import { FileReference } from '../../../../../../core/models/file-reference';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';
import { FileApiService } from '../../../../../../core/services/api/file-api.service';
import { FileInputComponent } from '../../../../../../core/components/file-input/file-input.component';

@Component({
  selector: 'cc-edit-project-card',
  templateUrl: './edit-project-card.component.html',
  styleUrls: ['./edit-project-card.component.scss']
})
export class EditProjectCardComponent implements OnInit {

  @Input()
  project: Project;

  @ViewChild('input')
  input: FileInputComponent

  logo: File;
  logoReference: FileReference;

  form = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required]),
    description: new UntypedFormControl('')
  });

  constructor(private projectStore: ProjectStoreService,
              private toastService: ToastService,
              private fileApi: FileApiService) {
  }

  ngOnInit(): void {
    this.form.get('name').setValue(this.project.name);
    this.form.get('description').setValue(this.project.description);
    this.logoReference = this.project.logo;
  }

  update(): void {
    const input: UpdateProjectInput = this.form.value;

    if (this.logo != null) {
      this.fileApi.create(this.logo).subscribe({
        next: (file: FileReference) => {
          input.logo = file;
          this.projectStore.updateProject(input);
          this.logoReference = file;
          this.input.reset();
        },
        error: err => {
          this.toastService.show({type: ToastType.DANGER, message: `The logo could not be uploaded.\n ${err.message}`});
        }
      });
    } else {
      input.logo = null;
      this.projectStore.updateProject(input);
    }
  }

  handleFileSelect(files: File[]): void {
    this.logo = files.length === 0 ? null : files[0];
  }

  removeLogo(e): void {
    if (e) e.preventDefault();
    this.logo = null;
    this.logoReference = null;
  }

  get logoStyle(): any {
    return {
      backgroundImage: `url(${this.project.logo?.downloadPath})`,
      backgroundSize: 'cover',
      width: '100px',
      height: '100px'
    };
  }
}
