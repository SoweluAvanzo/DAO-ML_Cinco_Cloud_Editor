import { Component, Input } from '@angular/core';
import { Project } from '../../../../core/models/project';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { User } from '../../../../core/models/user';

@UntilDestroy()
@Component({
  selector: 'cc-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent {

  @Input()
  projects: Project[] = [];

  currentUser: User;

  constructor(private appStore: AppStoreService) {
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.currentUser = user
    });
  }

  isUserOwnerOfProject(project: Project) {
    return this.currentUser != null && project.owner != null && project.owner.id === this.currentUser.id;
  }

  getProjectBackgroundImageStyle(project: Project): string {
    return project.logo != null
      ? `background-image: url(${project.logo.downloadPath}); background-size: cover`
      : '';
  }
}
