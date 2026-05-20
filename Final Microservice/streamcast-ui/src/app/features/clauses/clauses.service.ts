import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Clause } from '../../core/models/clause';
import { ApiResponse } from '../../core/models/contract';

@Injectable({ providedIn: 'root' })
export class ClausesService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/clauses`;

  list(): Observable<Clause[]> {
    return this.http.get<ApiResponse<Clause[]>>(`${this.base}/get`).pipe(map(r => r.data));
  }
  create(c: Clause): Observable<Clause> {
    return this.http.post<ApiResponse<Clause>>(`${this.base}/post`, c).pipe(map(r => r.data));
  }
}
