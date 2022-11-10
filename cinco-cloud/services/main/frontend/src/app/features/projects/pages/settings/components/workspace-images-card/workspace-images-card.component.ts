import { Component, Input } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { WorkspaceImage } from '../../../../../../core/models/workspace-image';
import { WorkspaceImageApiService } from '../../../../../../core/services/api/workspace-image-api.service';
import { ToastService, ToastType } from '../../../../../../core/services/toast.service';

@Component({
  selector: 'cc-workspace-images-card',
  templateUrl: './workspace-images-card.component.html',
})
export class WorkspaceImagesCardComponent {

  @Input()
  project: Project;

  constructor(private workspaceImageApi: WorkspaceImageApiService,
              private toastService: ToastService) {
  }

  handleToggle(image: WorkspaceImage): void {
    this.workspaceImageApi.update(image, {
      published: !image.published
    }).subscribe({
      next: updatedImage => {
        this.project.image = updatedImage;
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: `The image is now ${updatedImage.published ? 'public' : 'private'}`
        });
      },
      error: res => this.toastService.show({
        type: ToastType.DANGER,
        message: `The status could not be changed: ${res.error.message}`
      })
    });
  }
}
