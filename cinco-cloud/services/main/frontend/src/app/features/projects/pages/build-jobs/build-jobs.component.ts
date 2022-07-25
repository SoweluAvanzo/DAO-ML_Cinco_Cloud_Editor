import { Component, OnInit } from '@angular/core';
import { User } from '../../../../core/models/user';
import { Project } from '../../../../core/models/project';
import { WorkspaceImageBuildJob } from '../../../../core/models/workspace-image-build-job';
import { Page } from '../../../../core/models/page';
import { ProjectStoreService } from '../../services/project-store.service';
import { AppStoreService } from '../../../../core/services/stores/app-store.service';
import {
  WorkspaceImageBuildJobApiService
} from '../../../../core/services/api/workspace-image-build-job-api.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { filter, fromEvent } from 'rxjs';
import { WebSocketMessage } from '../../../../core/models/web-socket-message';
import { WebSocketEvent } from '../../../../core/enums/web-socket-event';
import { fromJsog } from '../../../../core/utils/jsog-utils';
import { Organization } from '../../../../core/models/organization';
import { ToastService, ToastType } from '../../../../core/services/toast.service';

@UntilDestroy()
@Component({
  selector: 'cc-build-jobs',
  templateUrl: './build-jobs.component.html'
})
export class BuildJobsComponent implements OnInit {

  user: User;
  project: Project;
  buildJobsPage: Page<WorkspaceImageBuildJob>;
  projectWebSocket: WebSocket;

  pageNumber = 0;
  pageSize = 15;

  constructor(private projectStore: ProjectStoreService,
              private appStore: AppStoreService,
              private buildJobApi: WorkspaceImageBuildJobApiService,
              private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => {
        this.project = project;
        this.loadPage();
      }
    });
    this.projectStore.projectWebSocket$.pipe(untilDestroyed(this), filter(ws => ws != null)).subscribe({
      next: ws => {
        this.projectWebSocket = ws;
        this.listenForMessages();
      }
    });
    this.appStore.user$.pipe(untilDestroyed(this)).subscribe({
      next: user => this.user = user
    });
  }

  listenForMessages() {
    fromEvent(this.projectWebSocket, 'message').pipe(untilDestroyed(this)).subscribe({
      next: (e: any) => {
        const message = WebSocketMessage.fromJson(e.data);

        switch (message.event) {
          case WebSocketEvent.UPDATE_BUILD_JOB_STATUS:
            const job: WorkspaceImageBuildJob = fromJsog(message.content, WorkspaceImageBuildJob);
            const i = this.buildJobsPage.items.findIndex(j => j.id == job.id);
            if (i > -1) {
              this.buildJobsPage.items[i] = job;
            } else {
              // insert job at the top if we are on the first page
              if (this.buildJobsPage.number == 0) {
                this.buildJobsPage.items.unshift(job);
                // remove the last job on the page if the page overflows
                if (this.buildJobsPage.items.length > this.pageSize) {
                  this.buildJobsPage.items.pop();
                  // increase the amount of pages if we are on the last page
                  if (this.buildJobsPage.number + 1 == this.buildJobsPage.amountOfPages) {
                    this.buildJobsPage.amountOfPages++;
                  }
                }
              }
            }
            break;
        }
      }
    });
  }

  loadPage(): void {
    this.buildJobApi.getAll(this.project.id, this.pageNumber, this.pageSize).subscribe({
      next: page => this.buildJobsPage = page,
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not fetch build-jobs.`
        });
        console.error(res.error.message);
      }
    });
  }

  nextPage(): void {
    this.pageNumber++;
    this.loadPage();
  }

  previousPage(): void {
    this.pageNumber = Math.max(0, this.pageNumber - 1);
    this.loadPage();
  }

  abortJob(job: WorkspaceImageBuildJob): void {
    this.buildJobApi.abort(this.project.id, job).subscribe({
      next: abortedJob => {
        this.toastService.show({type: ToastType.SUCCESS, message: 'The job has been aborted.'});
        job.status = abortedJob.status;
      },
      error: () => {
        this.toastService.show({type: ToastType.DANGER, message: 'The job could not be aborted.'});
      }
    });
  }

  deleteJob(job: WorkspaceImageBuildJob): void {
    this.buildJobApi.delete(this.project.id, job).subscribe({
      next: () => {
        this.toastService.show({type: ToastType.SUCCESS, message: 'The job has been deleted.'});
        this.buildJobsPage.items = this.buildJobsPage.items.filter(j => j.id !== job.id);
      },
      error: () => {
        this.toastService.show({type: ToastType.DANGER, message: 'The job could not be deleted.'});
      }
    });
  }

  get organization(): Organization {
    return this.project == null ? null : this.project.organization;
  }

  get hasNextPage(): boolean {
    return this.buildJobsPage != null && this.buildJobsPage.number + 1 < this.buildJobsPage.amountOfPages;
  }

  get hasPreviousPage(): boolean {
    return this.buildJobsPage != null && this.buildJobsPage.number > 0;
  }

}
