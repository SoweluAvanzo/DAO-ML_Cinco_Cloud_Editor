import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { WorkspaceImageApiService } from '../../../../../core/services/api/workspace-image-api.service';
import { WorkspaceImage } from '../../../../../core/models/workspace-image';

@Component({
  selector: 'cc-featured-projects',
  templateUrl: './featured-projects.component.html'
})
export class FeaturedProjectsComponent implements OnInit {

  @Output() selected = new EventEmitter<WorkspaceImage>();

  imagesGrid: WorkspaceImage[][] = [];

  constructor(private workspaceImageApi: WorkspaceImageApiService) {
  }

  ngOnInit(): void {
    this.workspaceImageApi.getAll(true).subscribe({
      next: images => {
        this.imagesGrid = this.splitIntoSubLists(images, 2);
      }
    });
  }

  style(image: WorkspaceImage) {
    return {
      height: '100%',
      width: '140px',
      backgroundImage: image.project.logo == null
        ? null
        : `url(${image.project.logo.downloadPath})`,
      backgroundSize: 'cover'
    }
  }

  private splitIntoSubLists<T>(list: T[], n: number): T[][] {
    if (n <= 0) throw new Error("The sublist length should be greater than 0.");

    let result: T[][] = [];

    for (let i = 0; i < list.length; i += n) {
      let sublist: T[] = [];
      for (let j = 0; j < n && i + j < list.length; j++) {
        sublist.push(list[i + j]);
      }
      result.push(sublist);
    }

    return result;
  }
}
