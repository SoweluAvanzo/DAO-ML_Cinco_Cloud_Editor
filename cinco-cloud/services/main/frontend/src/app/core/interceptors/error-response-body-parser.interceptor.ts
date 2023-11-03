import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable()
export class ErrorResponseBodyParserInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(catchError((response: HttpErrorResponse): Observable<any> => {
      if (response instanceof HttpErrorResponse) {

        // Attempt to parse the error response as JSON, falling back to the raw response if parsing fails.
        let error: any;
        try {
          error = JSON.parse(response.error);
        } catch (e) {
          error = response.error;
        }

        const parsedResponse = new HttpErrorResponse({
          error,
          url: response.url,
          status: response.status,
          headers: response.headers,
          statusText: response.statusText
        });

        return throwError(() => parsedResponse);
      }
      return throwError(() => response);
    }));
  }
}
