import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { WorkspaceImage } from '../../models/workspace-image';
import { fromJsog, fromJsogList } from '../../utils/jsog-utils';
import { UpdateWorkspaceImageInput } from '../../models/inputs/update-workspace-image-input';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceImageApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public search(query: string): Observable<WorkspaceImage[]> {
    return this.http.get(`${this.apiUrl}/images/search?q=${query}`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public getAll(): Observable<WorkspaceImage[]> {
    return this.http.get(`${this.apiUrl}/images`, this.defaultHttpOptions).pipe(
      map((body: any) => this.transformList(body))
    );
  }

  public update(image: WorkspaceImage, input: UpdateWorkspaceImageInput): Observable<WorkspaceImage> {
    return this.http.put(`${this.apiUrl}/images/${image.id}`, input, this.defaultHttpOptions).pipe(
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
