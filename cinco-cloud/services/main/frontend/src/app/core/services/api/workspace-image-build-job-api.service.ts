import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Page } from '../../models/page';
import { WorkspaceImageBuildJob } from '../../models/workspace-image-build-job';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';
import { WorkspaceImageBuildJobLog } from '../../models/workspace-image-build-job-log';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceImageBuildJobApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public get(projectId: number, jobId: number): Observable<WorkspaceImageBuildJob> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/build-jobs/${jobId}/private`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    )
  }

  public getAll(projectId: number, page: number, size: number): Observable<Page<WorkspaceImageBuildJob>> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/build-jobs/private?page=${page}&size=${size}`, this.defaultHttpOptions).pipe(
      map(body => this.transformPage(body))
    );
  }

  public update(projectId: number, job: WorkspaceImageBuildJob): Observable<WorkspaceImageBuildJob> {
    return this.http.put(`${this.apiUrl}/projects/${projectId}/build-jobs/private`, toJsog(job), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public getBuildLog(projectId: number, jobId: number): Observable<WorkspaceImageBuildJobLog> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/build-jobs/${jobId}/log/private`, this.defaultHttpOptions).pipe(
      map(body => fromJsog(body, WorkspaceImageBuildJobLog))
    );
  }

  public delete(projectId: number, job: WorkspaceImageBuildJob): Observable<WorkspaceImageBuildJob> {
    return this.http.delete(`${this.apiUrl}/projects/${projectId}/build-jobs/${job.id}/private`, this.defaultHttpOptions).pipe(
      map(_ => job)
    );
  }

  public abort(projectId: number, job: WorkspaceImageBuildJob): Observable<WorkspaceImageBuildJob> {
    return this.http.post(`${this.apiUrl}/projects/${projectId}/build-jobs/${job.id}/abort/private`, null, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformPage(body: any): Page<WorkspaceImageBuildJob> {
    const page = new Page<WorkspaceImageBuildJob>();
    page.number = body.number;
    page.size = body.size;
    page.amountOfPages = body.amountOfPages;
    page.items = this.transformList(body.items);
    return page;
  }

  private transformSingle(body: any): WorkspaceImageBuildJob {
    return fromJsog(body, WorkspaceImageBuildJob);
  }

  private transformList(body: any[]): WorkspaceImageBuildJob[] {
    return fromJsogList(body, WorkspaceImageBuildJob);
  }
}
