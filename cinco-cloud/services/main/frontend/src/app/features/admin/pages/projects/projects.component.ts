import { Component, OnInit } from '@angular/core';
import { WorkspaceImageApiService } from '../../../../core/services/api/workspace-image-api.service';
import { WorkspaceImage } from '../../../../core/models/workspace-image';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { loadingFor } from '@ngneat/loadoff';

@Component({
  selector: 'cc-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {
  loader = loadingFor('get');

  images: WorkspaceImage[] = [];

  constructor(private workspaceImageApiService: WorkspaceImageApiService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.workspaceImageApiService.getAll()
      .pipe(this.loader.get.track())
      .subscribe({
        next: images => {
          this.images = images
          console.log(this.images)
        },
        error: err => {
          this.toastService.show({
            type: ToastType.DANGER,
            message: `Failed to load images: ${err.message}`
          });
        }
      });
  }

  updateFeatured(image: WorkspaceImage, featured: boolean): void {
    this.workspaceImageApiService.update(image, {
      published: image.published,
      featured
    }).subscribe({
      next: updatedImage => {
        image.featured = updatedImage.featured;
        this.toastService.show({
          type: ToastType.SUCCESS,
          message: updatedImage.featured
            ? `Image has been marked as featured.`
            : `Image is no longer marked as featured.`
        });
      },
      error: err => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Failed to update image: ${err.message}`
        });
      }
    });
  }
}
