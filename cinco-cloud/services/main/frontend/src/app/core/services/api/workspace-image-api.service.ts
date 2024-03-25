import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { WorkspaceImage } from '../../models/workspace-image';
import { fromJsog, fromJsogList } from '../../utils/jsog-utils';
import { UpdateWorkspaceImageInput } from '../../models/inputs/update-workspace-image-input';
import { Page } from '../../models/page';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceImageApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  public search(search: string, page = 0, size = 25): Observable<Page<WorkspaceImage>> {
    const options = {
      params: { page, size, search },
      ...this.defaultHttpOptions
    }

    return this.http.get(`${this.apiUrl}/images`, options).pipe(
      map((body: any) => this.transformPage(body))
    );
  }

  public getAll(featured = false, page = 0, size = 25): Observable<Page<WorkspaceImage>> {
    const params = { page, size };
    if (featured) params['featured'] = featured;
    const options = {
      params,
      ...this.defaultHttpOptions
    }

    return this.http.get(`${this.apiUrl}/images`, options).pipe(
      map((body: any) => this.transformPage(body))
    );
  }

  public update(image: WorkspaceImage, input: UpdateWorkspaceImageInput): Observable<WorkspaceImage> {
    return this.http.put(`${this.apiUrl}/images/${image.id}`, input, this.defaultHttpOptions).pipe(
      map(body => this.transformSingle(body))
    );
  }

  private transformPage(body: any): Page<WorkspaceImage> {
    return Page.fromObject(body, this.transformList(body.items));
  }

  private transformSingle(body: any): WorkspaceImage {
    return fromJsog(body, WorkspaceImage);
  }

  private transformList(body: any[]): WorkspaceImage[] {
    return fromJsogList(body, WorkspaceImage);
  }
}
