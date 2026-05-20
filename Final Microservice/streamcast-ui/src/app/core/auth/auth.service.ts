import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CurrentUser, LoginRequest, Role } from '../models/user';
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

  login(req: LoginRequest): Observable<string> {
    return this.http
      .post(`${environment.apiBase}/auth/login`, req, { responseType: 'text' })
      .pipe(tap(token => this.persist(token)));
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  hasAnyRole(roles: Role[]): boolean {
    const r = this._user()?.role;
    return !!r && roles.includes(r);
  }

  private persist(token: string): void {
    const claims = decodeJwt(token);
    if (!claims) {
      throw new Error('Invalid token');
    }
    const user: CurrentUser = {
      email: claims.sub,
      role: claims.role as Role,
      token,
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
