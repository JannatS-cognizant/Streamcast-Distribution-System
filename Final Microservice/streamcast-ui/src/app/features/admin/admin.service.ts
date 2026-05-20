import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RoleDef, UserCreate, UserDef } from '../../core/models/admin';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private api = environment.apiBase;

  listUsers(): Observable<UserDef[]>    { return this.http.get<UserDef[]>(`${this.api}/users`); }
  createUser(u: UserCreate): Observable<UserDef> {
    return this.http.post<UserDef>(`${this.api}/users`, u);
  }
  deleteUser(id: number): Observable<string> {
    return this.http.delete(`${this.api}/users/${id}`, { responseType: 'text' });
  }

  listRoles(): Observable<RoleDef[]>    { return this.http.get<RoleDef[]>(`${this.api}/roles`); }
  createRole(name: string): Observable<RoleDef> {
    return this.http.post<RoleDef>(`${this.api}/roles`, { name });
  }
  deleteRole(id: number): Observable<string> {
    return this.http.delete(`${this.api}/roles/${id}`, { responseType: 'text' });
  }
}
