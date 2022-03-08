import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { map, mergeMap, Observable, tap } from 'rxjs';
import { UserLoginInput } from '../../models/forms/user-login-input';
import { AuthResponse } from '../../models/auth-response';
import { TicketResponse } from '../../models/ticket-response';
import { UserApiService } from './user-api.service';
import { User } from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService extends BaseApiService {

  static readonly TOKEN_KEY: string = 'cinco_cloud_token';

  constructor(http: HttpClient, private userApi: UserApiService) {
    super(http);
  }

  public login(input: UserLoginInput): Observable<User> {
    return this.http.post(`${this.apiUrl}/user/current/login`, input, this.defaultHttpOptions).pipe(
      map((body: any) => body as AuthResponse),
      tap(auth => {
        window.localStorage.setItem(AuthApiService.TOKEN_KEY, auth.token);
      }),
      mergeMap(_ => this.userApi.getCurrent())
    );
  }

  public logout(): Observable<boolean> {
    return this.http.get(`${this.apiUrl}/user/current/logout`, this.defaultHttpOptions).pipe(
      map(_ => true),
      tap(_ => {
        window.localStorage.removeItem(AuthApiService.TOKEN_KEY);
      })
    );
  }

  public getTicket(): Observable<TicketResponse> {
    return this.http.get(`${this.apiUrl}/ticket`, this.defaultHttpOptions).pipe(
      map((body: any) => body as TicketResponse)
    );
  }

  public getToken(): string {
    return window.localStorage.getItem(AuthApiService.TOKEN_KEY);
  }
}
