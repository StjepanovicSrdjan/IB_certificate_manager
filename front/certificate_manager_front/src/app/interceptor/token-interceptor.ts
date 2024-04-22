import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpInterceptor,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';

import { throwError, Observable, BehaviorSubject, of, finalize } from "rxjs";
import { catchError, filter, take, switchMap } from "rxjs/operators";
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';


@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  isRefresh: boolean = false;

  constructor(private auth: AuthService,
    public snackBar: MatSnackBar,
    private router: Router) { }
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (this.auth.isLoggedIn()) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${this.auth.getToken()}` 
        }
      });
    }
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error && error.status == 401) {
          return this.handle401Error(request, next);
        } else {
          return throwError(error);
        }
     })
    )
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefresh) {
      this.isRefresh = true;

      if (this.auth.isLoggedIn()) {
        return this.auth.refresh().pipe(
          switchMap((res) => {
            this.isRefresh = false;
            localStorage.setItem('user', JSON.stringify(res.accessToken));
            localStorage.setItem('refreshToken', JSON.stringify(res.refreshToken));
            this.auth.setUser();
            request = request.clone({
              setHeaders: {
                Authorization: `Bearer ${this.auth.getToken()}` 
              }
            });
            return next.handle(request);
          }),
          catchError((error) => {
            this.isRefresh = false;
            localStorage.removeItem('user');
              localStorage.removeItem('refreshToken');
              this.auth.setUser();
              this.router.navigate(['login']);
              this.snackBar.open("Your access token has expired!", "", {
                  duration: 2000,
              });

            return throwError(() => error);
          })
        );
      }
    }

    return next.handle(request);
  }
}
