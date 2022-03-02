import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { OrganizationAccessRightVector } from '../../models/organization-access-right-vector';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';

@Injectable({
  providedIn: 'root'
})
export class OrganizationAccessRightVectorApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
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
    return this.http.put(`${this.apiUrl}/organization/${vector.organization.id}/accessRights/${vector.id}`, toJsog(vector), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): OrganizationAccessRightVector {
    return fromJsog(body, OrganizationAccessRightVector);
  }

  private transformList(body: any[]): OrganizationAccessRightVector[] {
    return fromJsogList(body, OrganizationAccessRightVector);
  }
}
