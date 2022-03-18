import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { Page } from '../../../../../../core/models/page';
import { WorkspaceImageBuildJob } from '../../../../../../core/models/workspace-image-build-job';
import { ProjectStoreService } from '../../../../services/project-store.service';
import { AppStoreService } from '../../../../../../core/services/stores/app-store.service';
import { WorkspaceImageBuildJobApiService } from '../../../../../../core/services/api/workspace-image-build-job-api.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { filter, fromEvent } from 'rxjs';
import { WebSocketMessage } from '../../../../../../core/models/web-socket-message';
import { WebSocketEvent } from '../../../../../../core/enums/web-socket-event';
import { fromJsog } from '../../../../../../core/utils/jsog-utils';

@UntilDestroy()
@Component({
  selector: 'cc-status-widget',
  templateUrl: './status-widget.component.html',
  styleUrls: ['./status-widget.component.scss']
})
export class StatusWidgetComponent implements OnInit {

  @Input()
  project: Project;
  job: WorkspaceImageBuildJob;
  projectWebSocket: WebSocket;

  constructor(private projectStore: ProjectStoreService,
              private appStore: AppStoreService,
              private buildJobApi: WorkspaceImageBuildJobApiService) {
  }

  ngOnInit(): void {
    this.buildJobApi.getAll(this.project.id, 1, 1).subscribe({
      next: (test: Page<WorkspaceImageBuildJob>) => {
        this.job = test.items.sort((job1, job2) => {
          if (job1.startedAt > job2.startedAt) {
            return 1;
          }
          if (job1.startedAt < job2.startedAt) {
            return -1;
          }
          return 0;
        })[0];
      }
    });

    this.projectStore.projectWebSocket$.pipe(untilDestroyed(this), filter(ws => ws != null)).subscribe({
      next: ws => {
        this.projectWebSocket = ws;
        this.listenForMessages();
      }
    });
  }

  listenForMessages() {
    fromEvent(this.projectWebSocket, 'message').pipe(untilDestroyed(this)).subscribe({
      next: (e: any) => {
        const message = WebSocketMessage.fromJson(e.data);

        switch (message.event) {
          case WebSocketEvent.UPDATE_BUILD_JOB_STATUS:
            const job: WorkspaceImageBuildJob = fromJsog(message.content, WorkspaceImageBuildJob);
            this.job = job;
            break;
        }
      }
    });
  }

}
