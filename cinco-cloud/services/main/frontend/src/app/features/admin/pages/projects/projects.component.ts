import { Component, OnInit } from '@angular/core';
import { WorkspaceImageApiService } from '../../../../core/services/api/workspace-image-api.service';
import { WorkspaceImage } from '../../../../core/models/workspace-image';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { loadingFor } from '@ngneat/loadoff';
import { Page } from '../../../../core/models/page';

@Component({
  selector: 'cc-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {
  loader = loadingFor('get');

  imagesPage: Page<WorkspaceImage>;

  constructor(private workspaceImageApiService: WorkspaceImageApiService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(index = 0, size = 25) {
    this.workspaceImageApiService.getAll(false, index, size)
      .pipe(this.loader.get.track())
      .subscribe({
        next: page => {
          this.imagesPage = page;
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
