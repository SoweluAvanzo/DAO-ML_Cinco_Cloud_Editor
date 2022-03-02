import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { AppStoreService } from '../services/stores/app-store.service';

@Injectable({
  providedIn: 'root'
})
export class UserIsAdminGuard implements CanActivate, CanActivateChild {

  constructor(private appStore: AppStoreService,
              private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.userIsAdmin();
  }
  canActivateChild(
    childRoute: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.userIsAdmin();
  }

  private userIsAdmin(): boolean {
    const user = this.appStore.getUser();
    if (user != null && user.isAdmin) {
      return true;
    } else {
      this.router.navigate(['/app']);
      return false;
    }
  }
}
