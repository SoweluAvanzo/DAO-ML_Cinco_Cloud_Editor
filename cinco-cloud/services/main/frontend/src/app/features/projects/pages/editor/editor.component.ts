import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { ProjectStoreService } from '../../services/project-store.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { ProjectApiService } from '../../../../core/services/api/project-api.service';
import { Project } from '../../../../core/models/project';
import { ProjectDeployment } from '../../../../core/models/project-deployment';
import { WebSocketMessage } from '../../../../core/models/web-socket-message';
import { WebSocketEvent } from '../../../../core/enums/web-socket-event';
import { AuthApiService } from '../../../../core/services/api/auth-api.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { filter, fromEvent } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { WorkspaceImageBuildJob } from '../../../../core/models/workspace-image-build-job';
import { fromJsog } from '../../../../core/utils/jsog-utils';
import { Page } from '../../../../core/models/page';
import { WorkspaceImageBuildJobApiService } from '../../../../core/services/api/workspace-image-build-job-api.service';
import { ToastService, ToastType } from '../../../../core/services/toast.service';
import { Router } from '@angular/router';

@UntilDestroy()
@Component({
  selector: 'cc-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit, OnDestroy {

  @ViewChild('editorFrame')
  editorFrame: ElementRef;

  project: Project;
  deployment: ProjectDeployment;
  editorUrl: SafeResourceUrl;
  currentJob: WorkspaceImageBuildJob;

  redeploy: boolean = false;
  showEditor: boolean = false;
  interval: number = -1;

  constructor(private projectStore: ProjectStoreService,
              private authApi: AuthApiService,
              private projectApi: ProjectApiService,
              private domSanitizer: DomSanitizer,
              private buildJobApi: WorkspaceImageBuildJobApiService,
              private toastService: ToastService,
              private router: Router) {
    const currentNavigation = this.router.getCurrentNavigation();
    this.redeploy = !!currentNavigation?.extras.state?.['redeploy'];
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => {
        this.project = project;
        this.deploy();
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not connect to the project.`
        });
        console.log(res.error.message)
      }
    });

    this.buildJobApi.getAll(this.project.id, 0, 1).subscribe({
      next: (test: Page<WorkspaceImageBuildJob>) => {
        if (test.items.length < 1) {
          this.currentJob = null;
        } else {
          this.currentJob = test.items[0]
        }
      }
    });

    this.projectStore.projectWebSocket$.pipe(
      untilDestroyed(this),
      filter(ws => ws != null)
    ).subscribe({
      next: ws => {
        fromEvent(ws, 'message').pipe(untilDestroyed(this)).subscribe({
          next: (e: any) => {
            const message = WebSocketMessage.fromJson(e.data);
            switch (message.event) {
              case WebSocketEvent.UPDATE_POD_DEPLOYMENT_STATUS:
                this.deployment = message.content;
                this.waitForTheiaToBeReady();
                break;
              case WebSocketEvent.UPDATE_BUILD_JOB_STATUS:
                const job: WorkspaceImageBuildJob = fromJsog(message.content, WorkspaceImageBuildJob);
                this.currentJob = job;
                break;
              default:
                break;
            }
          }
        });
      }
    });
  }

  ngOnDestroy() {
    this.projectStore.reloadProject();
    window.clearInterval(this.interval);
  }

  deploy(): void {
    this.projectApi.deploy(this.project, this.redeploy).subscribe({
      next: deployment => {
        this.deployment = deployment;
        const url = environment.baseUrl + deployment.url + '?jwt=' + this.authApi.getToken() + '&projectId=' + this.project.id;
        this.editorUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(url);
        this.waitForTheiaToBeReady();
      },
      error: res => {
        this.toastService.show({
          type: ToastType.DANGER,
          message: `Could not deploy the project.`
        });
        console.log(res.error.message)
      }
    });
  }

  private waitForTheiaToBeReady() {
    if (this.deployment.status === 'READY') {

      const f = () => {
        if (!this.showEditor && this.editorFrame != null) {
          const innerHtml = this.editorFrame.nativeElement.contentWindow.document.body.innerHTML;
          if (innerHtml.includes('theia-preload')) {
            this.showEditor = true;
          } else if (innerHtml.includes('Service Temporarily Unavailable')) {
            this.editorFrame.nativeElement.contentWindow.location.reload();
          }
        } else {
          window.clearInterval(this.interval);
        }
      }

      f();
      this.interval = setInterval(f, 1000);
    }
  }
}
