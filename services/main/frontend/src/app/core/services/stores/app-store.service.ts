import { Injectable } from '@angular/core';
import { BehaviorSubject, from, mergeMap, Observable, tap } from 'rxjs';
import { User } from '../../models/user';
import { UserLoginInput } from '../../models/forms/user-login-input';
import { AuthApiService } from '../api/auth-api.service';
import { Router } from '@angular/router';
import { UserApiService } from '../api/user-api.service';

@Injectable({
  providedIn: 'root'
})
export class AppStoreService {

  private user = new BehaviorSubject<User>(null);

  constructor(private authApi: AuthApiService,
              private userApi: UserApiService,
              private router: Router) {
  }

  public get user$() {
    return this.user.asObservable();
  }

  public login(input: UserLoginInput): Observable<boolean> {
    return this.authApi.login(input).pipe(
      tap(user => this.user.next(user)),
      mergeMap(_ => from(this.router.navigate(['/app'])))
    );
  }

  public logout(): Observable<boolean> {
    return this.authApi.logout().pipe(
      mergeMap(_ => from(this.router.navigate(['/login'])))
    );
  }

  public setUser(user: User): void {
    this.user.next(user);
  }

  public getUser(): User {
    return this.user.getValue();
  }
}
