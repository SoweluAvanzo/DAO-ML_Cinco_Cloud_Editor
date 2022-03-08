import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Project } from '../../../core/models/project';
import { ProjectWebSocketApiService } from '../../../core/services/api/project-web-socket-api.service';

@Injectable()
export class ProjectStoreService {

  private project = new BehaviorSubject<Project>(null);
  private projectWebSocket = new BehaviorSubject<WebSocket>(null);

  constructor(private projectWebSocketApi: ProjectWebSocketApiService) {
  }

  get project$(): Observable<Project> {
    return this.project.asObservable();
  }

  get projectWebSocket$(): Observable<WebSocket> {
    return this.projectWebSocket.asObservable();
  }

  setProject(project: Project) {
    this.project.next(project);
  }

  initWebSocket(): void {
    this.projectWebSocketApi.create(this.project.value.id).subscribe({
      next: ws => this.projectWebSocket.next(ws),
      error: console.error
    });
  }

  closeWebSocket(): void {
    this.projectWebSocket.value?.close();
  }
}
