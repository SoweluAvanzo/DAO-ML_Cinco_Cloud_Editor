import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { WorkspaceImage } from '../../models/workspace-image';
import { fromJsog, fromJsogList, toJsog } from '../../utils/jsog-utils';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceImageApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public search(query: string): Observable<WorkspaceImage[]> {
    return this.http.get(`${this.apiUrl}/image-registry/search?q=${query}`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public getAll(): Observable<WorkspaceImage[]> {
    return this.http.get(`${this.apiUrl}/image-registry/images`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public update(image: WorkspaceImage): Observable<WorkspaceImage> {
    return this.http.put(`${this.apiUrl}/image-registry/images/${image.id}`, toJsog(image), this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformSingle(body: any): WorkspaceImage {
    return fromJsog(body, WorkspaceImage);
  }

  private transformList(body: any[]): WorkspaceImage[] {
    return fromJsogList(body, WorkspaceImage);
  }
}
