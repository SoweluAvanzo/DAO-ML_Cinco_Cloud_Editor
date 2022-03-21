import { Component, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Project } from '../../../../core/models/project';
import { User } from '../../../../core/models/user';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';

@UntilDestroy()
@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

  project: Project;
  user: User;

  constructor(public projectStore: ProjectStoreService,
              private appStore: AppStoreService) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => this.project = project
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }
}
