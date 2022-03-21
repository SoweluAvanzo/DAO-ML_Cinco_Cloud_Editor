import { Component, OnInit } from '@angular/core';
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

@UntilDestroy()
@Component({
  selector: 'cc-editor',
  templateUrl: './editor.component.html'

})
export class EditorComponent implements OnInit {

  project: Project;
  deployment: ProjectDeployment;
  editorUrl: SafeResourceUrl;

  constructor(private projectStore: ProjectStoreService,
              private authApi: AuthApiService,
              private projectApi: ProjectApiService,
              private domSanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.projectStore.project$.pipe(untilDestroyed(this)).subscribe({
      next: project => {
        this.project = project;
        this.deploy();
      },
      error: console.error
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
                break;
              default:
                break;
            }
          }
        });
      }
    });
  }

  deploy(): void {
    this.projectApi.deploy(this.project).subscribe({
      next: deployment => {
        this.deployment = deployment;
        const url = environment.baseUrl + deployment.url + '?jwt=' + this.authApi.getToken() + '&projectId=' + this.project.id;
        this.editorUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: console.error
    });
  }
}
