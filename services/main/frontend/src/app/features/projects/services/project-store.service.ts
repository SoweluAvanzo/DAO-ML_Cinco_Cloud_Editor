import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Project } from '../../../core/models/project';
import { ProjectWebSocketApiService } from '../../../core/services/api/project-web-socket-api.service';
import { User } from '../../../core/models/user';
import { ProjectApiService } from '../../../core/services/api/project-api.service';
import { OrganizationAccessRightVectorApiService } from '../../../core/services/api/organization-access-right-vector-api.service';
import { OrganizationAccessRightVector } from '../../../core/models/organization-access-right-vector';
import { UpdateProjectInput } from '../../../core/models/forms/update-project-input';
import { fromJsog, toJsog } from '../../../core/utils/jsog-utils';
import { Router } from '@angular/router';
import { AppStoreService } from '../../../core/services/stores/app-store.service';
import { OrganizationAccessRight } from '../../../core/enums/organization-access-right';
import { ModalUtilsService } from '../../../core/services/utils/modal-utils.service';
import { ToastService } from '../../../core/services/toast.service';

@Injectable()
export class ProjectStoreService {

  private project = new BehaviorSubject<Project>(null);
  private projectWebSocket = new BehaviorSubject<WebSocket>(null);
  private accessRights = new BehaviorSubject<OrganizationAccessRightVector>(null);

  constructor(private projectWebSocketApi: ProjectWebSocketApiService,
              private projectApi: ProjectApiService,
              private organizationARVApi: OrganizationAccessRightVectorApiService,
              private appStore: AppStoreService,
              private router: Router,
              private modalUtils: ModalUtilsService,
              private toastService: ToastService) {
  }

  get project$(): Observable<Project> {
    return this.project.asObservable();
  }

  get projectWebSocket$(): Observable<WebSocket> {
    return this.projectWebSocket.asObservable();
  }

  get accessRights$(): Observable<OrganizationAccessRightVector> {
    return this.accessRights.asObservable();
  }

  setProject(project: Project): void {
    this.project.next(project);
    if (project != null && project.organization != null) {
      this.organizationARVApi.getMy(project.organization.id).subscribe({
        next: accessRights => this.accessRights.next(accessRights)
      });
    }
  }

  updateProject(input: UpdateProjectInput): void {
    const copy: Project = fromJsog(toJsog(this.project.value), Project);
    copy.name = input.name;
    copy.description = input.description;
    this.projectApi.update(copy).subscribe({
      next: updatedProject => this.setProject(updatedProject)
    });
  }

  deleteProject(): void {
    this.modalUtils.confirm({
      text: 'Do you really want to delete this project?',
      confirmButtonText: 'Delete'
    }).then(() => {
      this.projectApi.remove(this.project.value).subscribe({
        next: () => this.afterLeaveOrDeleteProject(this.project.value),
        error: console.error
      });
    }).catch(() => {});
  }

  leaveProject(): void {
    this.modalUtils.confirm({
      text: 'Do you really want to leave this project?',
      confirmButtonText: 'Leave'
    }).then(() => {
      this.projectApi.removeMember(this.project.value.id, this.appStore.getUser()).subscribe({
        next: () => this.afterLeaveOrDeleteProject(this.project.value),
        error: console.error
      });
    }).catch(() => {})
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
    this.modalUtils.confirm({
      text: 'Do you really want to remove the user from the project?',
      confirmButtonText: 'Remove'
    }).then(() => {
      this.projectApi.removeMember(this.project.getValue().id, user).subscribe({
        next: project => this.project.next(project)
      });
    }).catch(() => {});
  }

  canUpdateProject(user: User): boolean {
    const project = this.project.value;
    const accessRights = this.accessRights.value;
    return user != null && ((project.owner != null && project.owner.id === user.id)
      || (project.organization != null && accessRights != null && accessRights.accessRights.includes(OrganizationAccessRight.EDIT_PROJECTS)));
  }

  canLeaveProject(user: User): boolean {
    return user != null
      && this.project.value.organization != null
      && this.project.value.isUserMember(user);
  }

  canDeleteProject(user: User): boolean {
    const project = this.project.value;
    const accessRights = this.accessRights.value;
    return user != null && ((project.owner != null && project.owner.id === user.id)
      || (project.organization != null && accessRights != null && accessRights.accessRights.includes(OrganizationAccessRight.DELETE_PROJECTS)));
  }

  canAddUsers(user: User): boolean {
    const project = this.project.value;
    if (user == null || project == null) return false;
    return project.owner != null && project.owner.id === user.id;
  }

  canDeleteUsers(user: User): boolean {
    return this.canAddUsers(user);
  }

  private afterLeaveOrDeleteProject(project: Project): void {
    this.project.next(null);
    this.accessRights.next(null);
    const redirectUrl = project.organization == null
      ? ['/app']
      : ['/app', 'organizations', project.organization.id];
    this.router.navigate(redirectUrl);
  }
}
