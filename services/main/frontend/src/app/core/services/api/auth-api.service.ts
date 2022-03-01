import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { JsogService } from 'jsog-typescript';
import { HttpClient } from '@angular/common/http';
import { map, Observable, tap } from 'rxjs';
import { UserLoginInput } from '../../models/forms/user-login-input';
import { AuthResponse } from '../../models/auth-response';
import { TicketResponse } from '../../models/ticket-response';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService extends BaseApiService {

  private readonly TOKEN_KEY: string = 'cinco_cloud_token';

  constructor(http: HttpClient, jsog: JsogService) {
    super(http, jsog);
  }

  public login(input: UserLoginInput): Observable<AuthResponse> {
    return this.http.post(`${this.apiUrl}/user/current/login`, input, this.defaultHttpOptions).pipe(
      map(body => body as AuthResponse),
      tap(auth => {
        window.localStorage.setItem(this.TOKEN_KEY, auth.token);
      })
    );
  }

  public logout(): Observable<boolean> {
    return this.http.get(`${this.apiUrl}/user/current/logout`, this.defaultHttpOptions).pipe(
      map(_ => true),
      tap(_ => {
        window.localStorage.removeItem(this.TOKEN_KEY);
      })
    );
  }

  public getTicket(): Observable<TicketResponse> {
    return this.http.get(`${this.apiUrl}/ticket`, this.defaultHttpOptions).pipe(
      map(body => body as TicketResponse)
    );
  }
}
