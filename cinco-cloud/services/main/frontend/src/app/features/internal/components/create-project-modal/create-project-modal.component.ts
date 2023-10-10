import { Component, Input } from '@angular/core';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { WorkspaceImage } from '../../../../core/models/workspace-image';
import { Project } from '../../../../core/models/project';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { Organization } from '../../../../core/models/organization';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { OrganizationApiService } from "../../../../core/services/api/organization-api.service";
import { faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'cc-create-project-modal',
  templateUrl: './create-project-modal.component.html',
  styleUrls: ['./create-project-modal.component.scss']
})
export class CreateProjectModalComponent {
  faTimes = faTimes;

  @Input()
  organization: Organization;

  form = new UntypedFormGroup({
    name: new UntypedFormControl('', [Validators.required]),
    description: new UntypedFormControl('')
  });

  withProjectImage: boolean = false;
  selectedProjectImage: WorkspaceImage;
  errorMessage: string = null;

  constructor(private projectApi: ProjectApiService,
              private organizationApi: OrganizationApiService,
              private appStore: AppStoreService,
              private toastService: ToastService,
              public modal: NgbActiveModal) {
  }

  get canCreateProject(): boolean {
    return this.withProjectImage
      ? this.selectedProjectImage != null && this.form.valid
      : this.form.valid;
  }

  createProject(): void {
    this.errorMessage = null;
    const newProject = new Project();
    newProject.name = this.form.value.name;
    newProject.description = this.form.value.description;
    newProject.owner = this.appStore.getUser();
    newProject.organization = this.organization;
    if (this.withProjectImage && this.selectedProjectImage != null) {
      newProject.template = this.selectedProjectImage;
    }

    let obs = this.organization == null
      ? this.projectApi.create(newProject)
      : this.organizationApi.createProject(newProject);

    obs.subscribe({
        next: createdProject => {
          this.toastService.show({
            message: `The project "${createdProject.name}" has been created.`,
            type: ToastType.SUCCESS
          });
          this.modal.close(createdProject);
        },
        error: res => {
          this.errorMessage = `The project could not be created: ${res.error.message}`;
        }
    });
  }
}
