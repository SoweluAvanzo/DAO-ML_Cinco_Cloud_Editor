import { Component, OnInit } from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Project } from '../../../../core/models/project';

@UntilDestroy()
@Component({
  selector: 'cc-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  project: Project;

  constructor(private projectStore: ProjectStoreService) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => this.project = project
    });
  }

}
