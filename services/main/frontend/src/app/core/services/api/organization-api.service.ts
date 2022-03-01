import {Injectable} from '@angular/core';
import {BaseApiService} from './base-api.service';
import {HttpClient} from '@angular/common/http';
import {JsogService} from 'jsog-typescript';
import {Organization} from '../../models/organization';
import {map, Observable} from 'rxjs';
import {User} from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class OrganizationApiService extends BaseApiService {

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public getAll(): Observable<Organization[]> {
    return this.http.get(`${this.apiUrl}/organization`, this.defaultHttpOptions).pipe(
      map(body => this.transformList(body as any[]))
    );
  }

  public get(organizationId: number): Observable<Organization> {
    return this.http.get(`${this.apiUrl}/organization/${organizationId}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public create(organization: Organization): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization`, this.jsog.serialize(organization), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public update(organization: Organization): Observable<Organization> {
    return this.http.put(`${this.apiUrl}/organization/${organization.id}`, this.jsog.serialize(organization), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public delete(organization: Organization): Observable<Organization> {
    return this.http.delete(`${this.apiUrl}/organization/${organization.id}`, this.defaultHttpOptions).pipe(
      map(body => organization)
    );
  }

  public leave(organization: Organization): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/leave`, '', this.defaultHttpOptions).pipe(
      map(body => organization)
    );
  }

  public addOwner(organization: Organization, user: User): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/removeUser`, this.jsog.serialize(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public addMember(organization: Organization, user: User): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/addMember`, this.jsog.serialize(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public removeUser(organization: Organization, user: User): Observable<Organization> {
    return this.http.post(`${this.apiUrl}/organization/${organization.id}/removeUser`, this.jsog.serialize(user), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): Organization {
    return this.jsog.deserializeObject(body as any, Organization);
  }

  private transformList(body: any[]): Organization[] {
    return this.jsog.deserializeArray(body as any[], Organization);
  }
}
