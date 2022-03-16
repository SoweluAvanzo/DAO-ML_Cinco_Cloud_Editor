import {Component, Input, OnInit} from '@angular/core';
import {Project} from "../../../../../../core/models/project";
import {User} from "../../../../../../core/models/user";
import {Page} from "../../../../../../core/models/page";
import {WorkspaceImageBuildJob} from "../../../../../../core/models/workspace-image-build-job";
import {ProjectStoreService} from "../../../../services/project-store.service";
import {AppStoreService} from "../../../../../../core/services/stores/app-store.service";
import {
  WorkspaceImageBuildJobApiService
} from "../../../../../../core/services/api/workspace-image-build-job-api.service";
import {untilDestroyed} from "@ngneat/until-destroy";
import {filter} from "rxjs";

@Component({
  selector: 'cc-status-widget',
  templateUrl: './status-widget.component.html',
  styleUrls: ['./status-widget.component.scss']
})
export class StatusWidgetComponent implements OnInit {

  @Input()
  project: Project;
  job: WorkspaceImageBuildJob;

  constructor(private projectStore: ProjectStoreService,
              private appStore: AppStoreService,
              private buildJobApi: WorkspaceImageBuildJobApiService) {
  }

  ngOnInit(): void {
    this.job = this.buildJobApi.getAll(this.project.id, 1, 1)[0]
  }

}
