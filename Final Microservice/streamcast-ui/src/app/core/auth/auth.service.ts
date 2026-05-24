import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AuthUserDTO,
  CurrentUser,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  Role
} from '../models/user';
import { decodeJwt } from './jwt.utils';

const STORAGE_KEY = 'sc.auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly _user = signal<CurrentUser | null>(this.restore());
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => {
    const u = this._user();
    return !!u && u.expiresAt > Date.now();
  });
  readonly role = computed<Role | null>(() => this._user()?.role ?? null);

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiBase}/auth/login`, req)
      .pipe(tap(res => this.persist(res)));
  }

  adminLogin(req: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiBase}/auth/admin/login`, req)
      .pipe(tap(res => this.persist(res)));
  }

  register(req: RegisterRequest): Observable<AuthUserDTO> {
    return this.http.post<AuthUserDTO>(`${environment.apiBase}/auth/register`, req);
  }

  verifyEmail(token: string): Observable<string> {
    return this.http.get(`${environment.apiBase}/auth/verify`, {
      params: new HttpParams().set('token', token),
      responseType: 'text'
    });
  }

  forgotPassword(email: string): Observable<string> {
    return this.http.post(`${environment.apiBase}/auth/forgot-password`, null, {
      params: new HttpParams().set('email', email),
      responseType: 'text'
    });
  }

  resetPassword(token: string, newPassword: string): Observable<string> {
    const params = new HttpParams()
      .set('token', token)
      .set('newPassword', newPassword);
    return this.http.post(`${environment.apiBase}/auth/reset-password`, null, {
      params,
      responseType: 'text'
    });
  }

  forgotUsername(email: string): Observable<string> {
    return this.http.post(`${environment.apiBase}/auth/forgot-username`, null, {
      params: new HttpParams().set('email', email),
      responseType: 'text'
    });
  }

  logout(): void {
    const wasAdmin = this._user()?.role === 'ADMIN';
    localStorage.removeItem(STORAGE_KEY);
    this._user.set(null);
    this.router.navigate([wasAdmin ? '/admin/login' : '/login']);
  }

  hasAnyRole(roles: Role[]): boolean {
    const r = this._user()?.role;
    return !!r && roles.includes(r);
  }

  private persist(res: LoginResponse): void {
    const claims = decodeJwt(res.token);
    if (!claims) {
      throw new Error('Invalid token');
    }
    const normalisedRole = (res.user?.role ?? claims.role ?? '')
      .toUpperCase()
      .replace(/\s+/g, '_') as Role;

    const user: CurrentUser = {
      id: res.user?.id,
      name: res.user?.name,
      email: res.user?.email ?? claims.sub,
      role: normalisedRole,
      emailVerified: res.user?.emailVerified,
      token: res.token,
      expiresAt: claims.exp * 1000
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    this._user.set(user);
  }

  private restore(): CurrentUser | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    try {
      const u = JSON.parse(raw) as CurrentUser;
      if (u.expiresAt <= Date.now()) {
        localStorage.removeItem(STORAGE_KEY);
        return null;
      }
      return u;
    } catch {
      return null;
    }
  }
}
