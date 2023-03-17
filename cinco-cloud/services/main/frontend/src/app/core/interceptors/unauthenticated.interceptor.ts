import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthApiService } from '../services/api/auth-api.service';
import { ToastService, ToastType } from '../services/toast.service';

@Injectable()
export class UnauthenticatedInterceptor implements HttpInterceptor {

  constructor(private router: Router,
              private authApi: AuthApiService,
              private toastService: ToastService) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if(request.url.endsWith('/auth')){
      return next.handle(request);
    } else {
      return next.handle(request).pipe(catchError((err: HttpErrorResponse): Observable<any> => {
        if (err instanceof HttpErrorResponse) {
          if (err.status === 401) {
            this.authApi.removeToken();
            this.router.navigate(['/']);
            this.toastService.show({
              type: ToastType.DANGER,
              message: `Unauthorized User`
            });
            return of(err.message);
          }
        }
        return throwError(() => err);
      }));
    }
  }
}
