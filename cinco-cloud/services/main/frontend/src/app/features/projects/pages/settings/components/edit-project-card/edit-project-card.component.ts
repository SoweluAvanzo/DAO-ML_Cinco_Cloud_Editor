import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { FormControl, FormGroup, Validators} from '@angular/forms';
import { ProjectStoreService } from '../../../../services/project-store.service';
import { FileReference } from '../../../../../../core/models/file-reference';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';
import { FileApiService } from '../../../../../../core/services/api/file-api.service';
import { FileInputComponent } from '../../../../../../core/components/file-input/file-input.component';

interface EditProjectForm {
  name: FormControl<string>;
  description?: FormControl<string>;
}

@Component({
  selector: 'cc-edit-project-card',
  templateUrl: './edit-project-card.component.html'
})
export class EditProjectCardComponent implements OnInit {

  @Input()
  project: Project;

  @ViewChild('input')
  input: FileInputComponent

  logo: File;
  logoReference: FileReference;
  logoChanged = false;

  form = new FormGroup<EditProjectForm>({
    name: new FormControl('', [Validators.required, Validators.minLength(1)]),
    description: new FormControl(null, { })
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
    const formValue = this.form.value;
    if (this.logo != null) {
      this.fileApi.create(this.logo).subscribe({
        next: (file: FileReference) => {
          this.projectStore.updateProject({
            logoId: file.id,
            name: formValue.name,
            description: formValue.description
          });
          this.logoReference = file;
          this.input.reset();
        },
        error: err => {
          this.toastService.show({type: ToastType.DANGER, message: `The logo could not be uploaded.\n ${err.message}`});
        }
      });
    } else {
      this.projectStore.updateProject({
        logoId: this.logoChanged ? this.logoReference?.id : this.project.logo?.id,
        name: formValue.name,
        description: formValue.description
      });
    }
  }

  handleFileSelect(files: File[]): void {
    this.logo = files.length === 0 ? null : files[0];
    this.logoChanged = true;
  }

  removeLogo(e): void {
    if (e) e.preventDefault();
    this.logo = null;
    this.logoReference = null;
    this.logoChanged = true;
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
