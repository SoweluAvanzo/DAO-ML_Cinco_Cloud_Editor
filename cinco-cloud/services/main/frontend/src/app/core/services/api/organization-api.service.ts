import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { Organization } from '../../models/organization';
import { map, Observable } from 'rxjs';
import { User } from '../../models/user';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';
import { BooleanResponse } from "../../models/boolean-response";

@Injectable({
  providedIn: 'root'
})
export class OrganizationApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public getAll(): Observable<Organization[]> {
    return this.http.get(`${this.apiUrl}/organization`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public get(organizationId: number): Observable<Organization> {
    return this.http.get(`${this.apiUrl}/organization/${organizationId}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public create(organization: Organization): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization`, toJsog(organization), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public update(organization: Organization): Observable<Organization> {
    return this.http.put(`${this.apiUrl}/organization/${organization.id}`, toJsog(organization), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public delete(organization: Organization): Observable<Organization> {
    return this.http.delete(`${this.apiUrl}/organization/${organization.id}`, this.defaultHttpOptions).pipe(
      map(body => organization)
    );
  }

  public leave(organization: Organization): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/leave`, null, this.defaultHttpOptions).pipe(
      map(body => organization)
    );
  }

  public addOwner(organization: Organization, user: User): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/addOwner`, toJsog(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public addMember(organization: Organization, user: User): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/addMember`, toJsog(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public removeUser(organization: Organization, user: User): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/removeUser`, toJsog(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public hasActiveBuildJobs(organization: Organization): Observable<BooleanResponse> {
    return this.http.get(`${this.apiUrl}/organization/${organization.id}/rpc/has-active-build-jobs/private`, this.defaultHttpOptions).pipe(
      map((body: any) => body as BooleanResponse)
    );
  }

  private transformSingle(body: any): Organization {
    return fromJsog(body, Organization);
  }

  private transformList(body: any[]): Organization[] {
    return fromJsogList(body, Organization);
  }
}
