import { Component } from '@angular/core';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { WorkspaceImage } from '../../../../core/models/workspace-image';
import { Project } from '../../../../core/models/project';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';

@Component({
  selector: 'cc-create-project-modal',
  templateUrl: './create-project-modal.component.html',
  styleUrls: ['./create-project-modal.component.scss']
})
export class CreateProjectModalComponent {

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    description: new FormControl('')
  });

  withProjectImage: boolean = false;
  selectedProjectImage: WorkspaceImage;

  constructor(private projectApi: ProjectApiService,
              private appStore: AppStoreService,
              public modal: NgbActiveModal) {
  }

  get canCreateProject(): boolean {
    return this.withProjectImage
      ? this.selectedProjectImage != null && this.form.valid
      : this.form.valid;
  }

  createProject(): void {
    const newProject = new Project();
    newProject.name = this.form.value.name;
    newProject.description = this.form.value.description;
    newProject.owner = this.appStore.getUser();
    if (this.withProjectImage && this.selectedProjectImage != null) {
      newProject.template = this.selectedProjectImage;
    }
    this.projectApi.create(newProject).subscribe({
      next: createdProject => this.modal.close(createdProject),
      error: console.error
    });
  }
}
