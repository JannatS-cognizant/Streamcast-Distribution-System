import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../core/models/contract';
import { Partner } from '../../core/models/partner';

@Injectable({ providedIn: 'root' })
export class PartnersService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/partners`;

  list(): Observable<Partner[]> {
    return this.http.get<ApiResponse<Partner[]>>(this.base).pipe(map(r => r.data));
  }
  create(p: Partner): Observable<Partner> {
    return this.http.post<ApiResponse<Partner>>(this.base, p).pipe(map(r => r.data));
  }
}
