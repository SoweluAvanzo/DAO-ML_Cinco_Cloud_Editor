import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../../../../../core/models/project';
import { Page } from '../../../../../../core/models/page';
import { WorkspaceImageBuildJob } from '../../../../../../core/models/workspace-image-build-job';
import { ProjectStoreService } from '../../../../services/project-store.service';
import { AppStoreService } from '../../../../../../core/services/stores/app-store.service';
import {
  WorkspaceImageBuildJobApiService
} from '../../../../../../core/services/api/workspace-image-build-job-api.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { filter, fromEvent } from 'rxjs';
import { WebSocketMessage } from '../../../../../../core/models/web-socket-message';
import { WebSocketEvent } from '../../../../../../core/enums/web-socket-event';
import { fromJsog } from '../../../../../../core/utils/jsog-utils';
import { ToastService } from '../../../../../../core/services/toast.service';

@UntilDestroy()
@Component({
  selector: 'cc-status-widget',
  templateUrl: './status-widget.component.html'
})
export class StatusWidgetComponent implements OnInit {

  @Input()
  project: Project;
  job: WorkspaceImageBuildJob;
  projectWebSocket: WebSocket;

  constructor(private projectStore: ProjectStoreService,
              private appStore: AppStoreService,
              private toastService: ToastService,
              private buildJobApi: WorkspaceImageBuildJobApiService) {
  }

  ngOnInit(): void {
    this.buildJobApi.getAll(this.project.id, 0, 1).subscribe({
      next: (test: Page<WorkspaceImageBuildJob>) => {
        if (test.items.length < 1) {
          this.job = null;
        } else {
          this.job = test.items[0]
        }
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
