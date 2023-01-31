import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { OrganizationAccessRightVector } from '../../models/organization-access-right-vector';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';
import { User } from "../../models/user";

@Injectable({
  providedIn: 'root'
})
export class OrganizationAccessRightVectorApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public getAll(organizationId: number): Observable<OrganizationAccessRightVector[]> {
    return this.http.get(`${this.apiUrl}/organizations/${organizationId}/access-rights`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public getMy(user: User, organizationId: number): Observable<OrganizationAccessRightVector> {
    return this.http.get(`${this.apiUrl}/organizations/${organizationId}/access-rights?user-id=${user.id}`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public update(vector: OrganizationAccessRightVector): Observable<OrganizationAccessRightVector> {
    return this.http.put(`${this.apiUrl}/organizations/${vector.organization.id}/access-rights/${vector.id}`, toJsog(vector), this.defaultHttpOptions).pipe(
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
