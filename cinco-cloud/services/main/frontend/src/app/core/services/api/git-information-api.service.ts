import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { GitInformation } from '../../models/git-information';
import { map, Observable } from 'rxjs';
import { fromJsog, toJsog } from '../../utils/jsog-utils';

@Injectable({
  providedIn: 'root'
})
export class GitInformationApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  get(projectId: number): Observable<GitInformation> {
    return this.http.get(`${this.apiUrl}/projects/${projectId}/git-information`, this.defaultHttpOptions).pipe(
      map(info => fromJsog(info, GitInformation))
    );
  }

  update(information: GitInformation): Observable<GitInformation> {
    return this.http.put(`${this.apiUrl}/projects/${information.projectId}/git-information`, toJsog(information), this.defaultHttpOptions).pipe(
      map(info => fromJsog(info, GitInformation))
    );
  }
}
