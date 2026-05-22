import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.user()?.token;

  const cloned = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  // Public auth endpoints (login/register/forgot/reset/verify) should not
  // trigger an auto-logout on 4xx — there is no session to invalidate yet.
  const isPublicAuthCall = /\/auth\/(login|register|verify|forgot-password|reset-password|forgot-username)\b/
    .test(req.url);

  return next(cloned).pipe(
    catchError(err => {
      if (err?.status === 401 && !isPublicAuthCall) {
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
