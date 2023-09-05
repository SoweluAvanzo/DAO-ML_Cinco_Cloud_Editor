import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Organization } from '../../../core/models/organization';
import { OrganizationApiService } from '../../../core/services/api/organization-api.service';

@Injectable({
  providedIn: 'root'
})
export class OrganizationResolver  {

  constructor(private organizationApi: OrganizationApiService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Organization> {
    const organizationId = Number(route.paramMap.get('organizationId'));
    return this.organizationApi.get(organizationId);
  }
}
