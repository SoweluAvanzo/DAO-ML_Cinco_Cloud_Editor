import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Project } from '../../models/project';
import { ProjectDeployment } from '../../models/project-deployment';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';
import { User } from '../../models/user';
import { Organization } from '../../models/organization';
import { BooleanResponse } from "../../models/boolean-response";

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

  public deploy(project: Project, redeploy: boolean = false): Observable<ProjectDeployment> {
    const options = { ...this.defaultHttpOptions, params: { redeploy } };
    return this.http.post(`${this.apiUrl}/project/${project.id}/deployments/private`, null, options).pipe(
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

  public transferToUser(project: Project, user: User): Observable<Project> {
    return this.http.put(`${this.apiUrl}/project/${project.id}/owner/private`, toJsog(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    )
  }

  public transferToOrganization(project: Project, organization: Organization): Observable<Project> {
    return this.http.put(`${this.apiUrl}/project/${project.id}/organization/private`, toJsog(organization), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    )
  }

  public remove(project: Project): Observable<Project> {
    return this.http.get(`${this.apiUrl}/project/remove/${project.id}/private`, this.defaultHttpOptions).pipe(
      map(body => project)
    );
  }

  public addMember(projectId: number, user: User): Observable<Project> {
    return this.http.post(`${this.apiUrl}/project/${projectId}/member/private`, toJsog(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public removeMember(projectId: number, user: User): Observable<Project> {
    return this.http.delete(`${this.apiUrl}/project/${projectId}/member/${user.id}/private`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public hasActiveBuildJobs(projectId: number): Observable<BooleanResponse> {
    return this.http.get(`${this.apiUrl}/project/${projectId}/rpc/has-active-build-jobs/private`, this.defaultHttpOptions).pipe(
      map((body: any) => body as BooleanResponse)
    );
  }

  private transformSingle(body: any): Project {
    return fromJsog(body, Project);
  }

  private transformList(body: any[]): Project[] {
    return fromJsogList(body, Project);
  }
}
