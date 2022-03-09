import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Project } from '../../../core/models/project';
import { ProjectWebSocketApiService } from '../../../core/services/api/project-web-socket-api.service';
import { User } from '../../../core/models/user';
import { ProjectApiService } from '../../../core/services/api/project-api.service';

@Injectable()
export class ProjectStoreService {

  private project = new BehaviorSubject<Project>(null);
  private projectWebSocket = new BehaviorSubject<WebSocket>(null);

  constructor(private projectWebSocketApi: ProjectWebSocketApiService,
              private projectApi: ProjectApiService) {
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

  addProjectMember(user: User): void {
    this.projectApi.addMember(this.project.getValue().id, user).subscribe({
      next: project => this.project.next(project)
    });
  }

  removeProjectMember(user: User): void {
    this.projectApi.removeMember(this.project.getValue().id, user).subscribe({
      next: project => this.project.next(project)
    });
  }
}
