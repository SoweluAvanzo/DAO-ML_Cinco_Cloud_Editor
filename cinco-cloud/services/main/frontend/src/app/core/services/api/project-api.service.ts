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
import { Page } from '../../models/page';
import { UpdateProjectInput } from '../../tos/update-project-input';

@Injectable({
  providedIn: 'root'
})
export class ProjectApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public getAll(): Observable<Project[]> {
    return this.http.get(`${this.apiUrl}/projects`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformPage(body).items)
    );
  }

  public getAllPaged(page: number, size: number): Observable<Page<Project>> {
    const options = {
      ...this.defaultHttpOptions,
      params: { page, size }
    };

    return this.http.get(`${this.apiUrl}/projects`, options).pipe(
      map((body: any) => this.transformPage(body))
    );
  }

  public get(projectId: number): Observable<Project> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public create(project: Project): Observable<Project> {
    return this.http.post(`${this.apiUrl}/projects`, toJsog(project), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public deploy(project: Project, redeploy: boolean = false): Observable<ProjectDeployment> {
    const options = { ...this.defaultHttpOptions, params: { redeploy } };
    return this.http.post(`${this.apiUrl}/projects/${project.id}/deployments`, null, options).pipe(
      map((body: any) => body as ProjectDeployment)
    );
  }

  public stop(project: Project): Observable<string> {
    return this.http.delete(`${this.apiUrl}/projects/${project.id}/deployments`, this.defaultHttpOptions).pipe(
      map(body => '')
    );
  }

  public update(project: Project, input: UpdateProjectInput): Observable<Project> {
    return this.http.put(`${this.apiUrl}/projects/${project.id}`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public transferToUser(project: Project, user: User): Observable<Project> {
    return this.http.put(`${this.apiUrl}/projects/${project.id}/rpc/transfer-to-user`,{ userId: user.id }, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    )
  }

  public transferToOrganization(project: Project, organization: Organization): Observable<Project> {
    return this.http.put(`${this.apiUrl}/projects/${project.id}/rpc/transfer-to-organization`, { orgId: organization.id } , this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    )
  }

  public remove(project: Project): Observable<Project> {
    return this.http.delete(`${this.apiUrl}/projects/${project.id}`, this.defaultHttpOptions).pipe(
      map(body => project)
    );
  }

  public addMember(projectId: number, user: User): Observable<Project> {
    return this.http.post(`${this.apiUrl}/projects/${projectId}/members`, { userId: user.id }, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public removeMember(projectId: number, user: User): Observable<Project> {
    return this.http.delete(`${this.apiUrl}/projects/${projectId}/members/${user.id}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public hasActiveBuildJobs(projectId: number): Observable<BooleanResponse> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/rpc/has-active-build-jobs`, this.defaultHttpOptions).pipe(
      map((body: any) => body as BooleanResponse)
    );
  }

  private transformPage(body: any): Page<Project> {
    return Page.fromObject(body, this.transformList(body.items));
  }

  private transformSingle(body: any): Project {
    return fromJsog(body, Project);
  }

  private transformList(body: any[]): Project[] {
    return fromJsogList(body, Project);
  }
}
