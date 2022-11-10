import { Component, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Project } from '../../../../core/models/project';
import { User } from '../../../../core/models/user';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import { ProjectApiService } from "../../../../core/services/api/project-api.service";
import { filter } from 'rxjs';

@UntilDestroy()
@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

  project: Project;
  user: User;
  hasActiveBuildJobs: boolean;

  constructor(public projectStore: ProjectStoreService,
              public projectApi: ProjectApiService,
              private appStore: AppStoreService) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this), filter(p => p != null)).subscribe({
      next: project => {
        this.project = project;
        this.projectApi.hasActiveBuildJobs(project.id).subscribe({
          next: res => this.hasActiveBuildJobs = res.value
        })
      }
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }
}
