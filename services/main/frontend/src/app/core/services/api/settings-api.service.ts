import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Settings } from '../../models/settings';
import { fromJsog, toJsog } from '../../utils/jsog-utils';

@Injectable({
  providedIn: 'root'
})
export class SettingsApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public get(): Observable<Settings> {
    return this.http.get(`${this.apiUrl}/settings/public`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public update(settings: Settings): Observable<Settings> {
    return this.http.put(`${this.apiUrl}/settings`, toJsog(settings), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): Settings {
    return fromJsog(body, Settings);
  }
}
