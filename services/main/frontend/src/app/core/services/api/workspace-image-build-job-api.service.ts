import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { JsogService } from 'jsog-typescript';
import { map, Observable } from 'rxjs';
import { Page } from '../../models/page';
import { WorkspaceImageBuildJob } from '../../models/workspace-image-build-job';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceImageBuildJobApiService extends BaseApiService {

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public getAll(projectId: number, page: number, size: number): Observable<Page<WorkspaceImageBuildJob>> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/build-jobs/private?page=${page}&size=${size}`, this.defaultHttpOptions).pipe(
      map(body => this.transformPage(body))
    );
  }

  public update(projectId: number, job: WorkspaceImageBuildJob): Observable<WorkspaceImageBuildJob> {
    return this.http.put(`${this.apiUrl}/projects/${projectId}/build-jobs/private`, this.jsog.serialize(job), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public delete(projectId: number, job: WorkspaceImageBuildJob): Observable<WorkspaceImageBuildJob> {
    return this.http.delete(`${this.apiUrl}/projects/${projectId}/build-jobs/${job.id}/private`, this.defaultHttpOptions).pipe(
      map(_ => job)
    );
  }

  public abort(projectId: number, job: WorkspaceImageBuildJob): Observable<WorkspaceImageBuildJob> {
    return this.http.post(`${this.apiUrl}/projects/${projectId}/build-jobs/${job.id}/abort/private`, '', this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformPage(body: any): Page<WorkspaceImageBuildJob> {
    const page = body as Page<WorkspaceImageBuildJob>;
    page.items = this.transformList(page.items);
    return page;
  }

  private transformSingle(body: any): WorkspaceImageBuildJob {
    return this.jsog.deserializeObject(body as any, WorkspaceImageBuildJob);
  }

  private transformList(body: any[]): WorkspaceImageBuildJob[] {
    return this.jsog.deserializeArray(body as any[], WorkspaceImageBuildJob);
  }
}
