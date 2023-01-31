import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient, HttpEvent, HttpEventType, HttpProgressEvent, HttpResponse } from '@angular/common/http';
import { BehaviorSubject, filter, map, Observable } from 'rxjs';
import { FileReference } from '../../models/file-reference';
import { fromJsog } from '../../utils/jsog-utils';

export enum UploadState {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE'
}

export interface Upload {
  progress: number
  state: UploadState,
  file: FileReference
}

@Injectable({
  providedIn: 'root'
})
export class FileApiService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  create(file: File): Observable<FileReference> {
    return this.upload(file).pipe(
      filter((u: Upload) => u.file != null),
      map((u: Upload) => u.file)
    );
  }

  upload(file: File): Observable<Upload> {
    const subject = new BehaviorSubject<Upload>({
      state: UploadState.PENDING,
      progress: 0,
      file: null
    });

    const formData = new FormData();
    formData.append('file', file);

    const options = {...this.defaultHttpOptions, ...{
      reportProgress: true,
      observe: 'events'
    }};

    const upload = this.http.post<HttpEvent<any>>(`${this.apiUrl}/files`, formData, options);
    upload.subscribe({
      next: (e: HttpEvent<any>) => {
        if (this.isHttpProgressEvent(e)) {
          subject.next({
            state: UploadState.IN_PROGRESS,
            progress: e.total ? Math.round((100 * e.loaded) / e.total) : 0,
            file: null
          });
        } else if (this.isHttpResponse(e)) {
          subject.next({
            state: UploadState.DONE,
            progress: 100,
            file: fromJsog(e.body, FileReference)
          });
          subject.complete();
        }
      },
      error: err => subject.error(err)
    });

    return subject.asObservable();
  }

  delete(file: FileReference): Observable<FileReference> {
    return this.http.delete(`${this.apiUrl}/files/${file.id}`, this.defaultHttpOptions).pipe(
      map(() => file)
    );
  }

  private isHttpProgressEvent(event: HttpEvent<any>): event is HttpProgressEvent {
    return event.type === HttpEventType.DownloadProgress
      || event.type === HttpEventType.UploadProgress
  }

  private isHttpResponse<T>(event: HttpEvent<T>): event is HttpResponse<T> {
    return event.type === HttpEventType.Response
  }
}
