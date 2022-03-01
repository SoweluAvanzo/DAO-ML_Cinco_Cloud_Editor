import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { JsogService } from 'jsog-typescript';
import { map, Observable } from 'rxjs';
import { Project } from '../../models/project';
import { ProjectDeployment } from '../../models/project-deployment';

@Injectable({
  providedIn: 'root'
})
export class ProjectApiService extends BaseApiService {

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public get(projectId: number): Observable<Project> {
    return this.http.get(`${this.apiUrl}/project/${projectId}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public create(project: Project): Observable<Project> {
    return this.http.post(`${this.apiUrl}/project/create/private`, this.jsog.serialize(project), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public deploy(project: Project): Observable<ProjectDeployment> {
    return this.http.post(`${this.apiUrl}/project/${project.id}/deployments/private`, '', this.defaultHttpOptions).pipe(
      map(body => body as ProjectDeployment)
    );
  }

  public stop(project: Project): Observable<string> {
    return this.http.delete(`${this.apiUrl}/project/${project.id}/deployments/private`, this.defaultHttpOptions).pipe(
      map(body => '')
    );
  }

  public update(project: Project): Observable<Project> {
    return this.http.post(`${this.apiUrl}/project/update/private`, this.jsog.serialize(project), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public remove(project: Project): Observable<Project> {
    return this.http.get(`${this.apiUrl}/project/remove/${project.id}/private`, this.defaultHttpOptions).pipe(
      map(body => project)
    );
  }

  private transformSingle(body: any): Project {
    return this.jsog.deserializeObject(body as any, Project);
  }

  private transformList(body: any[]): Project[] {
    return this.jsog.deserializeArray(body as any[], Project);
  }
}
