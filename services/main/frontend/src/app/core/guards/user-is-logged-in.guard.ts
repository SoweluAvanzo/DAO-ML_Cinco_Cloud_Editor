import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot, UrlTree } from '@angular/router';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { AuthApiService } from '../services/api/auth-api.service';
import { AppStoreService } from '../services/stores/app-store.service';
import { UserApiService } from '../services/api/user-api.service';

@Injectable({
  providedIn: 'root'
})
export class UserIsLoggedInGuard implements CanActivate, CanActivateChild {

  constructor(private userApi: UserApiService,
              private appStore: AppStoreService) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.tokenIsInLocalStorage();
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.tokenIsInLocalStorage();
  }

  private tokenIsInLocalStorage(): Observable<boolean> {
    const token = window.localStorage.getItem(AuthApiService.TOKEN_KEY);
    if (token == null) return of(false);
    return this.userApi.getCurrent().pipe(
      tap(user => this.appStore.setUser(user)),
      map(_ => true),
      catchError(_ => of(false))
    );
  }
}
