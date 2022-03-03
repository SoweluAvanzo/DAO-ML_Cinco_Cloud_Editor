import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { AuthApiService } from './auth-api.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectWebSocketApiService extends BaseApiService {

  constructor(http: HttpClient, private authService: AuthApiService) {
    super(http);
  }

  public create(projectId: number): Observable<WebSocket> {
    const subject = new Subject<WebSocket>();
    this.authService.getTicket().subscribe({
      next: ticketResponse => {
        const ticket = ticketResponse.ticket;
        const socket = new WebSocket(`${this.webSocketUrl}/project/${projectId}/${ticket}/private`);
        socket.addEventListener('onopen', e => {
          console.debug(`open projectWebsocket: ${e.toString()}`);
          subject.next(socket);
          subject.complete();
        });
        socket.addEventListener('onerror', e => {
          console.debug(`error opening projectWebsocket: ${e.toString()}`);
          throw e;
        });
      },
      error: err => {
        subject.error(err);
        subject.complete();
      }
    });
    return subject.asObservable();
  }
}
