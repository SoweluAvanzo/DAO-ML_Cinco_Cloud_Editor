import {Injectable} from '@angular/core';
import {BaseApiService} from './base-api.service';
import {HttpClient} from '@angular/common/http';
import {JsogService} from 'jsog-typescript';
import {map, Observable} from 'rxjs';
import {OrganizationAccessRightVector} from '../../models/organization-access-right-vector';

@Injectable({
  providedIn: 'root'
})
export class OrganizationAccessRightVectorApiService extends BaseApiService {

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public getAll(organizationId: number): Observable<OrganizationAccessRightVector[]> {
    return this.http.get(`${this.apiUrl}/organization/${organizationId}/accessRights`, this.defaultHttpOptions).pipe(
      map(body => this.transformList(body as any[]))
    );
  }

  public getMy(organizationId: number): Observable<OrganizationAccessRightVector> {
    return this.http.get(`${this.apiUrl}/organization/${organizationId}/accessRights/my`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public update(vector: OrganizationAccessRightVector): Observable<OrganizationAccessRightVector> {
    return this.http.put(`${this.apiUrl}/organization/${vector.organization.id}/accessRights/${vector.id}`, this.jsog.serialize(vector), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): OrganizationAccessRightVector {
    return this.jsog.deserializeObject(body as any, OrganizationAccessRightVector);
  }

  private transformList(body: any[]): OrganizationAccessRightVector[] {
    return this.jsog.deserializeArray(body as any[], OrganizationAccessRightVector);
  }
}
