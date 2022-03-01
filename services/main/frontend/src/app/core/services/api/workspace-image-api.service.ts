import {Injectable} from '@angular/core';
import {BaseApiService} from './base-api.service';
import {HttpClient} from '@angular/common/http';
import {JsogService} from 'jsog-typescript';
import {map, Observable} from 'rxjs';
import {WorkspaceImage} from '../../models/workspace-image';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceImageApiService extends BaseApiService {

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public search(query: string): Observable<WorkspaceImage[]> {
    return this.http.get(`${this.apiUrl}/image-registry/search?q=${query}`, this.defaultHttpOptions).pipe(
      map(body => this.transformList(body as any[]))
    );
  }

  public getAll(): Observable<WorkspaceImage[]> {
    return this.http.get(`${this.apiUrl}/image-registry/images`, this.defaultHttpOptions).pipe(
      map(body => this.transformList(body as any[]))
    );
  }

  public update(image: WorkspaceImage): Observable<WorkspaceImage> {
    return this.http.put(`${this.apiUrl}/image-registry/images/${image.id}`, this.jsog.serialize(image), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): WorkspaceImage {
    return this.jsog.deserializeObject(body as any, WorkspaceImage);
  }

  private transformList(body: any[]): WorkspaceImage[] {
    return this.jsog.deserializeArray(body as any[], WorkspaceImage);
  }
}
