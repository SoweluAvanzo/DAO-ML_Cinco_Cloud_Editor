import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { JsogService } from 'jsog-typescript';
import { map, Observable } from 'rxjs';
import { Settings } from '../../models/settings';

@Injectable({
  providedIn: 'root'
})
export class SettingsApiService extends BaseApiService {

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public get(): Observable<Settings> {
    return this.http.get(`${this.apiUrl}/settings/public`, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  public update(settings: Settings): Observable<Settings> {
    return this.http.put(`${this.apiUrl}/settings`, this.jsog.serialize(settings), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): Settings {
    return this.jsog.deserializeObject(body as any, Settings);
  }
}
