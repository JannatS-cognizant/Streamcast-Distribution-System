import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ComplianceCheck, ComplianceUpdate } from '../../core/models/compliance';
import { ApiResponse } from '../../core/models/contract';

@Injectable({ providedIn: 'root' })
export class ComplianceService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/compliance`;

  list(): Observable<ComplianceCheck[]> {
    return this.http.get<ApiResponse<ComplianceCheck[]>>(`${this.base}/checks`).pipe(map(r => r.data));
  }
  get(id: number): Observable<ComplianceCheck> {
    return this.http.get<ApiResponse<ComplianceCheck>>(`${this.base}/check/${id}`).pipe(map(r => r.data));
  }
  runCheck(c: ComplianceCheck): Observable<ComplianceCheck> {
    return this.http.post<ApiResponse<ComplianceCheck>>(`${this.base}/check`, c).pipe(map(r => r.data));
  }
  update(id: number, c: ComplianceUpdate): Observable<ComplianceCheck> {
    return this.http.put<ApiResponse<ComplianceCheck>>(`${this.base}/checks/${id}`, c).pipe(map(r => r.data));
  }
}
