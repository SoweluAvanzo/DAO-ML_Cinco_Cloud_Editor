import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Project } from '../../models/project';
import { ProjectDeployment } from '../../models/project-deployment';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';

@Injectable({
  providedIn: 'root'
})
export class ProjectApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public getAll(): Observable<Project[]> {
    return this.http.get(`${this.apiUrl}/project/private`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public get(projectId: number): Observable<Project> {
    return this.http.get(`${this.apiUrl}/project/${projectId}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public create(project: Project): Observable<Project> {
    return this.http.post(`${this.apiUrl}/project/create/private`, toJsog(project), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public deploy(project: Project): Observable<ProjectDeployment> {
    return this.http.post(`${this.apiUrl}/project/${project.id}/deployments/private`, '', this.defaultHttpOptions).pipe(
      map((body: any) => body as ProjectDeployment)
    );
  }

  public stop(project: Project): Observable<string> {
    return this.http.delete(`${this.apiUrl}/project/${project.id}/deployments/private`, this.defaultHttpOptions).pipe(
      map(body => '')
    );
  }

  public update(project: Project): Observable<Project> {
    return this.http.post(`${this.apiUrl}/project/update/private`, toJsog(project), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public remove(project: Project): Observable<Project> {
    return this.http.get(`${this.apiUrl}/project/remove/${project.id}/private`, this.defaultHttpOptions).pipe(
      map(body => project)
    );
  }

  private transformSingle(body: any): Project {
    return fromJsog(body, Project);
  }

  private transformList(body: any[]): Project[] {
    return fromJsogList(body, Project);
  }
}
