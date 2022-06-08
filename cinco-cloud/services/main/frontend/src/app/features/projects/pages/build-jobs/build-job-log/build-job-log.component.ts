import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { ProjectStoreService } from '../../../services/project-store.service';
import { combineLatest, filter, fromEvent } from 'rxjs';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { WebSocketMessage } from '../../../../../core/models/web-socket-message';
import { WebSocketEvent } from '../../../../../core/enums/web-socket-event';
import { fromJsog } from '../../../../../core/utils/jsog-utils';
import { WorkspaceImageBuildJobLogMessage } from '../../../../../core/models/workspace-image-build-job-log-message';
import {
  WorkspaceImageBuildJobApiService
} from '../../../../../core/services/api/workspace-image-build-job-api.service';
import { Project } from '../../../../../core/models/project';
import { WorkspaceImageBuildJobLog } from '../../../../../core/models/workspace-image-build-job-log';
import { ActivatedRoute } from '@angular/router';
import { WorkspaceImageBuildJob } from '../../../../../core/models/workspace-image-build-job';
import { DOCUMENT } from '@angular/common';

@UntilDestroy()
@Component({
  selector: 'cc-build-job-log',
  templateUrl: './build-job-log.component.html',
  styleUrls: ['./build-job-log.component.scss']
})
export class BuildJobLogComponent implements OnInit {

  @ViewChild('logWrapper') logWrapperEl: ElementRef;

  @ViewChild('logProgressIndicator') logProgressIndicatorEl: ElementRef;

  job: WorkspaceImageBuildJob;

  project: Project;

  projectWebSocket: WebSocket;

  buildJobLog: WorkspaceImageBuildJobLog;

  logMessages: String[] = [];

  private lastScrollTop: number = 0;

  private lockAtLogEnd: boolean = true;

  private logTimer: number = -1;

  constructor(private projectStore: ProjectStoreService,
              private buildJobsApi: WorkspaceImageBuildJobApiService,
              private route: ActivatedRoute,
              @Inject(DOCUMENT) private document: Document) {}

  ngOnInit(): void {
    combineLatest([
      this.route.params.pipe(untilDestroyed(this)),
      this.projectStore.project$.pipe(untilDestroyed(this)),
      this.projectStore.projectWebSocket$.pipe(untilDestroyed(this), filter(ws => ws != null))
    ]).subscribe({
      next: res => {
        this.project = res[1];
        this.projectWebSocket = res[2];
        this.buildJobsApi.get(this.project.id, res[0]['jobId']).subscribe({
          next: job => {
            this.job = job;
            this.listenForMessages();
            this.syncLogMessages();
          }
        })
      }
    })
  }

  listenForMessages(): void {
    fromEvent(this.projectWebSocket, 'message').pipe(untilDestroyed(this)).subscribe({
      next: (e: any) => {
        const message = WebSocketMessage.fromJson(e.data);

        switch(message.event) {
          case WebSocketEvent.BUILD_JOB_LOG_MESSAGE:
            const msg = fromJsog(message.content, WorkspaceImageBuildJobLogMessage) as WorkspaceImageBuildJobLogMessage;
            this.logMessages = [...this.logMessages, ...msg.logMessages];
            if (this.logTimer === -1) {
              this.logTimer = window.setTimeout(() => {
                if (this.lockAtLogEnd) this.scrollBottom();
                this.logTimer = -1;
              }, 500);
            }
            break;

          case WebSocketEvent.UPDATE_BUILD_JOB_STATUS:
            const job: WorkspaceImageBuildJob = fromJsog(message.content, WorkspaceImageBuildJob);
            if (job.id === this.job.id) {
              this.job = job;
            }
            break;
        }
      }
    });
  }

  handleScrollOnLogWrapper(e: any): void {
    if (e.target.scrollTop < this.lastScrollTop) {
      this.lockAtLogEnd = false;
    }
  }

  private syncLogMessages(): void {
    // clear log messages
    this.logMessages = [];

    // fetch the latest logs
    this.buildJobsApi.getBuildLog(this.project.id, this.job.id).subscribe(buildJobLog => {
      this.buildJobLog = buildJobLog;
      if (this.buildJobLog.log != null) {
        let log = this.buildJobLog.log.split(/\r?\n/);
        if (log[log.length-1] == '') log.pop();
        this.logMessages.unshift(...log);
      }
    });
  }

  scrollTop(): void {
    this.lockAtLogEnd = false;
    this.logWrapperEl?.nativeElement?.firstElementChild?.scrollIntoView();
  }

  scrollBottom(): void {
    this.lockAtLogEnd = true;
    this.lastScrollTop = this.logWrapperEl?.nativeElement?.scrollTop;
    if (this.logProgressIndicatorEl != null) {
      this.logProgressIndicatorEl?.nativeElement?.scrollIntoView();
    } else {
      this.logWrapperEl?.nativeElement?.lastElementChild?.scrollIntoView();
    }
  }

  downloadLog(): void {
    let logFile = [];
    this.logMessages.forEach(line => {
      logFile.push(line, '\n');
    })
    let logFileBlob = new Blob(logFile, { type: "text/plain" });
    let link = document.createElement('a');
    link.download = 'build-job-'.concat(this.job.id.toString()).concat('.log');
    link.href = document.defaultView.URL.createObjectURL(logFileBlob);
    link.click();
  }

  trackByIndex(index: number, el: any): number {
    return index;
  }
}
